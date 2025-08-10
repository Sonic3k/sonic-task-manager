package com.sonic.taskmanager.service;

import com.sonic.taskmanager.model.Task;
import com.sonic.taskmanager.model.dto.WorkspaceDto;
import com.sonic.taskmanager.repository.TaskRepository;
import com.sonic.taskmanager.util.DateUtils;
import com.sonic.taskmanager.util.FocusCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class WorkspaceService {

    private final TaskRepository taskRepository;
    private final FocusCalculator focusCalculator;
    private final ReminderService reminderService;
    private final MoodCalculatorService moodCalculatorService;

    public WorkspaceService(TaskRepository taskRepository, 
                           FocusCalculator focusCalculator,
                           ReminderService reminderService,
                           MoodCalculatorService moodCalculatorService) {
        this.taskRepository = taskRepository;
        this.focusCalculator = focusCalculator;
        this.reminderService = reminderService;
        this.moodCalculatorService = moodCalculatorService;
    }

    /**
     * Calculate today's complete workspace
     * This is the main method that frontend calls
     */
    public WorkspaceDto calculateTodaysWorkspace() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // Get all active tasks (not completed, not snoozed)
        List<Task> allActiveTasks = taskRepository.findActiveTasks(now);
        
        // Enrich tasks with calculated fields
        allActiveTasks = enrichTasksWithCalculatedFields(allActiveTasks);

        // Calculate main components
        Task focusTask = calculateFocusTask(allActiveTasks);
        List<Task> nextUpStack = calculateNextUpStack(allActiveTasks, focusTask);
        List<Task> quickWins = calculateQuickWins(allActiveTasks);
        List<Task> activeReminders = reminderService.getActiveReminders();
        String dailyMood = moodCalculatorService.calculateDailyMood(allActiveTasks);

        // Build workspace DTO
        WorkspaceDto workspace = new WorkspaceDto();
        workspace.setFocusTask(focusTask);
        workspace.setNextUpStack(nextUpStack);
        workspace.setQuickWins(quickWins);
        workspace.setActiveReminders(activeReminders);
        workspace.setDailyMood(dailyMood);

        // Determine which sections to show
        WorkspaceDto.ShowSectionsDto showSections = calculateShowSections(allActiveTasks, activeReminders, quickWins);
        workspace.setShowSections(showSections);

        // Calculate workload assessment
        WorkspaceDto.WorkloadAssessmentDto workloadAssessment = calculateWorkloadAssessment(allActiveTasks);
        workspace.setWorkloadAssessment(workloadAssessment);

        return workspace;
    }

    /**
     * Calculate the best focus task for today
     */
    private Task calculateFocusTask(List<Task> activeTasks) {
        // Filter to main tasks only (no subtasks)
        List<Task> mainTasks = activeTasks.stream()
                .filter(task -> task.getParentId() == null)
                .filter(task -> !"done".equals(task.getStatus()))
                .toList();

        Task focusTask = focusCalculator.calculateFocusTask(mainTasks);
        
        if (focusTask != null) {
            // Generate focus context if not already set
            if (focusTask.getFocusContext() == null || focusTask.getFocusContext().trim().isEmpty()) {
                focusTask.setFocusContext(focusCalculator.generateFocusContext(focusTask));
            }
            
            // Load subtasks for the focus task
            List<Task> subtasks = taskRepository.findByParentId(focusTask.getId());
            focusTask.setSubtasks(enrichTasksWithCalculatedFields(subtasks));
        }

        return focusTask;
    }

    /**
     * Calculate next up stack (other important tasks waiting)
     */
    private List<Task> calculateNextUpStack(List<Task> activeTasks, Task focusTask) {
        Long focusTaskId = focusTask != null ? focusTask.getId() : null;

        return activeTasks.stream()
                .filter(task -> task.getParentId() == null) // Main tasks only
                .filter(task -> !"done".equals(task.getStatus()))
                .filter(task -> !task.getId().equals(focusTaskId)) // Exclude focus task
                .filter(task -> !"reminder".equals(task.getType())) // Exclude reminders
                .sorted((t1, t2) -> {
                    // Sort by focus score (highest first)
                    double score1 = focusCalculator.calculateFocusScore(t1);
                    double score2 = focusCalculator.calculateFocusScore(t2);
                    return Double.compare(score2, score1);
                })
                .limit(8) // Limit to max 8 items in stack
                .toList();
    }

    /**
     * Calculate quick win tasks (high priority + easy)
     */
    private List<Task> calculateQuickWins(List<Task> activeTasks) {
        return activeTasks.stream()
                .filter(task -> task.getParentId() == null) // Main tasks only
                .filter(task -> "todo".equals(task.getStatus()))
                .filter(task -> "high".equals(task.getPriority()))
                .filter(task -> "easy".equals(task.getComplexity()))
                .limit(4) // Limit to 4 quick wins
                .toList();
    }

    /**
     * Determine which sections should be visible
     */
    private WorkspaceDto.ShowSectionsDto calculateShowSections(List<Task> allTasks, 
                                                              List<Task> reminders, 
                                                              List<Task> quickWins) {
        WorkspaceDto.ShowSectionsDto showSections = new WorkspaceDto.ShowSectionsDto();

        // Show urgent tasks if there are overdue or due today
        boolean hasUrgentTasks = allTasks.stream()
                .anyMatch(task -> task.isOverdue() || task.isUrgent());
        showSections.setUrgentTasks(hasUrgentTasks);

        // Show quick wins if available
        showSections.setQuickWins(!quickWins.isEmpty());

        // Show reminders if available
        showSections.setReminders(!reminders.isEmpty());

        // Show habits if there are any habit tasks
        boolean hasHabits = allTasks.stream()
                .anyMatch(task -> "habit".equals(task.getType()));
        showSections.setHabits(hasHabits);

        return showSections;
    }

    /**
     * Calculate workload assessment
     */
    private WorkspaceDto.WorkloadAssessmentDto calculateWorkloadAssessment(List<Task> allTasks) {
        WorkspaceDto.WorkloadAssessmentDto assessment = new WorkspaceDto.WorkloadAssessmentDto();

        // Count active main tasks
        List<Task> mainTasks = allTasks.stream()
                .filter(task -> task.getParentId() == null)
                .filter(task -> !"done".equals(task.getStatus()))
                .toList();

        assessment.setTotalTasks(mainTasks.size());

        // Count urgent tasks
        int urgentCount = (int) mainTasks.stream()
                .filter(task -> task.isOverdue() || task.isUrgent())
                .count();
        assessment.setUrgentCount(urgentCount);

        // Estimate total hours (rough calculation)
        double estimatedHours = mainTasks.stream()
                .mapToDouble(this::estimateTaskHours)
                .sum();
        assessment.setEstimatedHours(Math.round(estimatedHours * 10.0) / 10.0); // Round to 1 decimal

        // Generate recommendation
        String recommendation = generateWorkloadRecommendation(assessment);
        assessment.setRecommendation(recommendation);

        return assessment;
    }

    /**
     * Rough estimation of task hours based on complexity
     */
    private double estimateTaskHours(Task task) {
        if (task.getComplexity() == null) return 1.0;
        
        return switch (task.getComplexity().toLowerCase()) {
            case "easy" -> 0.5;
            case "medium" -> 2.0;
            case "hard" -> 4.0;
            default -> 1.0;
        };
    }

    /**
     * Generate workload recommendation
     */
    private String generateWorkloadRecommendation(WorkspaceDto.WorkloadAssessmentDto assessment) {
        if (assessment.getUrgentCount() > 3) {
            return "Heavy day with urgent tasks - focus on priorities";
        } else if (assessment.getUrgentCount() > 0) {
            return "Some urgent items to handle today";
        } else if (assessment.getTotalTasks() <= 3) {
            return "Light day, good for deep work or catching up";
        } else if (assessment.getTotalTasks() <= 6) {
            return "Moderate workload, balance focus and quick tasks";
        } else {
            return "Busy day ahead - consider prioritizing";
        }
    }

    /**
     * Enrich tasks with calculated fields
     */
    private List<Task> enrichTasksWithCalculatedFields(List<Task> tasks) {
        return tasks.stream()
                .map(this::enrichTaskWithCalculatedFields)
                .toList();
    }

    /**
     * Enrich single task with calculated fields
     */
    private Task enrichTaskWithCalculatedFields(Task task) {
        // Calculate days until deadline
        task.setDaysUntilDeadline(focusCalculator.calculateDaysUntilDeadline(task));

        // Calculate urgency level
        task.setUrgencyLevel(focusCalculator.calculateUrgencyLevel(task));

        return task;
    }

    /**
     * Refresh workspace calculation (can be called after task updates)
     */
    public WorkspaceDto refreshWorkspace() {
        return calculateTodaysWorkspace();
    }
}