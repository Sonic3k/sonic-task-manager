package com.sonic.taskmanager.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sonic.taskmanager.model.Task;
import com.sonic.taskmanager.model.dto.BulkOperationResultDto;
import com.sonic.taskmanager.model.dto.PaginationDto;
import com.sonic.taskmanager.model.dto.TaskDto;
import com.sonic.taskmanager.model.dto.TaskFilterDto;
import com.sonic.taskmanager.model.request.BulkUpdateRequest;
import com.sonic.taskmanager.model.request.CreateTaskRequest;
import com.sonic.taskmanager.model.response.PaginatedTaskResponse;
import com.sonic.taskmanager.repository.TaskRepository;
import com.sonic.taskmanager.util.DateUtils;
import com.sonic.taskmanager.util.FocusCalculator;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final FocusCalculator focusCalculator;

    public TaskService(TaskRepository taskRepository, FocusCalculator focusCalculator) {
        this.taskRepository = taskRepository;
        this.focusCalculator = focusCalculator;
    }

    /**
     * Get all active tasks (not completed, not snoozed)
     */
    @Transactional(readOnly = true)
    public List<Task> getAllActiveTasks() {
        List<Task> tasks = taskRepository.findActiveTasks(LocalDateTime.now());
        return enrichTasksWithCalculatedFields(tasks);
    }

    /**
     * Get task by ID
     */
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            enrichTaskWithCalculatedFields(task.get());
            // Load subtasks
            List<Task> subtasks = taskRepository.findByParentId(id);
            task.get().setSubtasks(enrichTasksWithCalculatedFields(subtasks));
        }
        return task;
    }

    /**
     * Create new task
     */
    public Task createTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setType(autoDetectTypeIfNeeded(request.getType(), request.getTitle()));
        task.setPriority(request.getPriority());
        task.setComplexity(autoDetectComplexityIfNeeded(request.getComplexity(), request.getTitle()));
        task.setParentId(request.getParentId());
        task.setDeadline(request.getDeadline());
        task.setScheduledDate(request.getScheduledDate());
        task.setFocusContext(request.getFocusContext());
        task.setTags(request.getTags());
        task.setContext(request.getContext());

        // Set default status
        task.setStatus("todo");

        // Set default progress
        if (task.getParentId() == null) {
            task.setProgressTotal(1); // Main tasks start with 1 total
        } else {
            task.setProgressTotal(1); // Subtasks are binary (0 or 1)
        }

        Task savedTask = taskRepository.save(task);

        // Update parent progress if this is a subtask
        if (savedTask.getParentId() != null) {
            updateParentProgress(savedTask.getParentId());
        }

        return enrichTaskWithCalculatedFields(savedTask);
    }

    /**
     * Update existing task
     */
    public Optional<Task> updateTask(Long id, CreateTaskRequest request) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isEmpty()) {
            return Optional.empty();
        }

        Task task = existingTask.get();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setType(request.getType());
        task.setPriority(request.getPriority());
        task.setComplexity(request.getComplexity());
        task.setDeadline(request.getDeadline());
        task.setScheduledDate(request.getScheduledDate());
        task.setFocusContext(request.getFocusContext());
        task.setTags(request.getTags());
        task.setContext(request.getContext());

        Task savedTask = taskRepository.save(task);
        return Optional.of(enrichTaskWithCalculatedFields(savedTask));
    }

    /**
     * Complete a task
     */
    public boolean completeTask(Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            return false;
        }

        Task task = taskOpt.get();
        task.setStatus("done");
        task.setCompletedAt(LocalDateTime.now());

        // If it's a subtask, set progress to total (completed)
        if (task.getParentId() != null) {
            task.setProgressCurrent(task.getProgressTotal());
        } else {
            // If it's a main task, complete all its subtasks too
            List<Task> subtasks = taskRepository.findByParentId(id);
            for (Task subtask : subtasks) {
                if (!"done".equals(subtask.getStatus())) {
                    subtask.setStatus("done");
                    subtask.setCompletedAt(LocalDateTime.now());
                    subtask.setProgressCurrent(subtask.getProgressTotal());
                    taskRepository.save(subtask);
                }
            }
            task.setProgressCurrent(task.getProgressTotal());
        }

        taskRepository.save(task);

        // Update parent progress if this is a subtask
        if (task.getParentId() != null) {
            updateParentProgress(task.getParentId());
        }

        return true;
    }

    /**
     * Snooze a task
     */
    public boolean snoozeTask(Long id, LocalDateTime snoozeUntil) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            return false;
        }

        Task task = taskOpt.get();
        task.setSnoozedUntil(snoozeUntil);
        task.setStatus("snoozed");
        taskRepository.save(task);
        return true;
    }

    /**
     * Delete a task
     */
    public boolean deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            return false;
        }

        // Get task to check if it has parent
        Optional<Task> task = taskRepository.findById(id);
        Long parentId = task.map(Task::getParentId).orElse(null);

        // Delete all subtasks first
        List<Task> subtasks = taskRepository.findByParentId(id);
        taskRepository.deleteAll(subtasks);

        // Delete the main task
        taskRepository.deleteById(id);

        // Update parent progress if this was a subtask
        if (parentId != null) {
            updateParentProgress(parentId);
        }

        return true;
    }

    /**
     * Get subtasks for a parent task
     */
    @Transactional(readOnly = true)
    public List<Task> getSubtasks(Long parentId) {
        List<Task> subtasks = taskRepository.findByParentId(parentId);
        return enrichTasksWithCalculatedFields(subtasks);
    }

    /**
     * Find quick win tasks (high priority + easy complexity)
     */
    @Transactional(readOnly = true)
    public List<Task> findQuickWinTasks() {
        List<Task> tasks = taskRepository.findQuickWinTasks();
        return enrichTasksWithCalculatedFields(tasks);
    }

    // === STUDIO METHODS - NEW ===

    /**
     * Get paginated tasks with filtering for Studio interface
     */
    @Transactional(readOnly = true)
    public PaginatedTaskResponse getTasksPaginated(Pageable pageable, TaskFilterDto filter) {
        Page<Task> taskPage;
        
        if (isFilterEmpty(filter)) {
            // No filter - get all main tasks with smart ordering
            taskPage = taskRepository.findAllMainTasksPaginated(
                DateUtils.today(),
                DateUtils.today().plusDays(3),
                pageable
            );
        } else {
            // Apply filters
            taskPage = taskRepository.findTasksWithFilter(
                filter.getStatus(),
                filter.getPriority(),
                filter.getComplexity(),
                filter.getType(),
                filter.getDeadlineFrom(),
                filter.getDeadlineTo(),
                filter.getSearchQuery(),
                DateUtils.today(),
                DateUtils.today().plusDays(3),
                pageable
            );
        }

        // Enrich tasks with calculated fields
        List<Task> enrichedTasks = enrichTasksWithCalculatedFields(taskPage.getContent());
        
        // Apply post-processing filters (for fields not in database)
        if (filter != null && (filter.getIsOverdue() != null || filter.getIsUrgent() != null || filter.getHasSubtasks() != null)) {
            enrichedTasks = enrichedTasks.stream()
                .filter(task -> matchesPostFilters(task, filter))
                .toList();
        }

        List<TaskDto> taskDtos = enrichedTasks.stream()
                .map(this::convertToDto)
                .toList();

        // Build pagination DTO
        PaginationDto pagination = new PaginationDto();
        pagination.setPage(taskPage.getNumber());
        pagination.setSize(taskPage.getSize());
        pagination.setTotalElements(taskPage.getTotalElements());
        pagination.setTotalPages(taskPage.getTotalPages());
        pagination.setFirst(taskPage.isFirst());
        pagination.setLast(taskPage.isLast());
        pagination.setHasNext(taskPage.hasNext());
        pagination.setHasPrevious(taskPage.hasPrevious());

        // Build response
        PaginatedTaskResponse response = new PaginatedTaskResponse();
        response.setSuccess(true);
        response.setTasks(taskDtos);
        response.setPagination(pagination);
        
        return response;
    }

    /**
     * Bulk update tasks for Studio interface
     */
    @Transactional
    public BulkOperationResultDto bulkUpdateTasks(BulkUpdateRequest request) {
        BulkOperationResultDto result = new BulkOperationResultDto(
            request.getOperation(), 
            request.getTaskIds().size()
        );

        List<Task> tasks = taskRepository.findByIdIn(request.getTaskIds());
        
        for (Task task : tasks) {
            try {
                boolean success = processBulkOperation(task, request);
                if (success) {
                    result.addSuccess(task.getId());
                } else {
                    result.addError("Failed to process task: " + task.getTitle());
                }
            } catch (Exception e) {
                result.addError("Error processing task " + task.getId() + ": " + e.getMessage());
            }
        }

        return result;
    }

    /**
     * Get task statistics for Studio dashboard
     */
    @Transactional(readOnly = true)
    public TaskStatsDto getTaskStatistics() {
        TaskStatsDto stats = new TaskStatsDto();
        
        stats.setTotalTasks(taskRepository.countMainTasksByStatus("todo") + 
                           taskRepository.countMainTasksByStatus("doing"));
        stats.setCompletedTasks(taskRepository.countMainTasksByStatus("done"));
        stats.setOverdueTasks(taskRepository.findOverdueTasks(DateUtils.today()).size());
        stats.setUrgentTasks(taskRepository.findUrgentTasks(DateUtils.today(), DateUtils.today().plusDays(3)).size());
        
        stats.setHighPriorityTasks(taskRepository.countMainTasksByPriority("high"));
        stats.setMediumPriorityTasks(taskRepository.countMainTasksByPriority("medium"));
        stats.setLowPriorityTasks(taskRepository.countMainTasksByPriority("low"));
        
        stats.setDeadlineTasks(taskRepository.countMainTasksByType("deadline"));
        stats.setHabitTasks(taskRepository.countMainTasksByType("habit"));
        stats.setReminderTasks(taskRepository.countMainTasksByType("reminder"));
        stats.setEventTasks(taskRepository.countMainTasksByType("event"));
        
        return stats;
    }

    /**
     * Update parent task progress based on subtasks completion
     */
    private void updateParentProgress(Long parentId) {
        Optional<Task> parentOpt = taskRepository.findById(parentId);
        if (parentOpt.isEmpty()) {
            return;
        }

        Task parent = parentOpt.get();
        List<Task> subtasks = taskRepository.findByParentId(parentId);

        if (subtasks.isEmpty()) {
            return;
        }

        // Count completed subtasks
        long completedSubtasks = subtasks.stream()
                .filter(task -> "done".equals(task.getStatus()))
                .count();

        parent.setProgressCurrent((int) completedSubtasks);
        parent.setProgressTotal(subtasks.size());

        // If all subtasks are done, mark parent as done too
        if (completedSubtasks == subtasks.size()) {
            parent.setStatus("done");
            parent.setCompletedAt(LocalDateTime.now());
        } else if (completedSubtasks > 0 && "todo".equals(parent.getStatus())) {
            // If some progress made, change status to 'doing'
            parent.setStatus("doing");
        }

        taskRepository.save(parent);
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
     * Auto-detect task type based on title keywords
     */
    private String autoDetectTypeIfNeeded(String currentType, String title) {
        if (currentType != null && !currentType.equals("deadline")) {
            return currentType; // Don't override if already set to something specific
        }

        if (title == null) return "deadline";

        String lowerTitle = title.toLowerCase();

        // Check for habit/learning keywords
        if (lowerTitle.contains("tập") || lowerTitle.contains("học") || 
            lowerTitle.contains("practice") || lowerTitle.contains("learn")) {
            return "habit";
        }

        // Check for reminder keywords
        if (lowerTitle.contains("nghĩ") || lowerTitle.contains("nhớ") || 
            lowerTitle.contains("consider") || lowerTitle.contains("think about")) {
            return "reminder";
        }

        // Default to deadline
        return "deadline";
    }

    /**
     * Auto-detect complexity based on title keywords
     */
    private String autoDetectComplexityIfNeeded(String currentComplexity, String title) {
        if (currentComplexity != null && !currentComplexity.equals("medium")) {
            return currentComplexity; // Don't override if explicitly set
        }

        if (title == null) return "medium";

        String lowerTitle = title.toLowerCase();

        // Easy task indicators
        if (lowerTitle.contains("reply") || lowerTitle.contains("email") || 
            lowerTitle.contains("call") || lowerTitle.contains("backup") ||
            lowerTitle.contains("gọi") || lowerTitle.contains("trả lời")) {
            return "easy";
        }

        // Hard task indicators
        if (lowerTitle.contains("design") || lowerTitle.contains("develop") || 
            lowerTitle.contains("research") || lowerTitle.contains("thiết kế") ||
            lowerTitle.contains("phát triển") || lowerTitle.contains("nghiên cứu")) {
            return "hard";
        }

        return "medium";
    }

    /**
     * Convert Task to TaskDto
     */
    public TaskDto convertToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setType(task.getType());
        dto.setPriority(task.getPriority());
        dto.setComplexity(task.getComplexity());
        dto.setStatus(task.getStatus());
        dto.setProgressCurrent(task.getProgressCurrent());
        dto.setProgressTotal(task.getProgressTotal());
        dto.setParentId(task.getParentId());
        dto.setDeadline(task.getDeadline());
        dto.setScheduledDate(task.getScheduledDate());
        dto.setCompletedAt(task.getCompletedAt());
        dto.setFocusContext(task.getFocusContext());
        dto.setTags(task.getTags());
        dto.setContext(task.getContext());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        // Calculated fields
        dto.setProgressPercentage(task.getProgressPercentage());
        dto.setDaysUntilDeadline(task.getDaysUntilDeadline());
        dto.setUrgencyLevel(task.getUrgencyLevel());
        dto.setComplexityLabel(task.getComplexityLabel());
        dto.setOverdue(task.isOverdue());
        dto.setUrgent(task.isUrgent());

        return dto;
    }

    // === STUDIO HELPER METHODS ===

    private boolean isFilterEmpty(TaskFilterDto filter) {
        return filter == null ||
               (filter.getStatus() == null &&
                filter.getPriority() == null &&
                filter.getComplexity() == null &&
                filter.getType() == null &&
                filter.getDeadlineFrom() == null &&
                filter.getDeadlineTo() == null &&
                filter.getSearchQuery() == null &&
                filter.getHasSubtasks() == null &&
                filter.getIsOverdue() == null &&
                filter.getIsUrgent() == null);
    }

    private boolean matchesPostFilters(Task task, TaskFilterDto filter) {
        if (filter.getIsOverdue() != null && filter.getIsOverdue() != task.isOverdue()) {
            return false;
        }
        if (filter.getIsUrgent() != null && filter.getIsUrgent() != task.isUrgent()) {
            return false;
        }
        if (filter.getHasSubtasks() != null) {
            List<Task> subtasks = taskRepository.findByParentId(task.getId());
            boolean hasSubtasks = !subtasks.isEmpty();
            if (filter.getHasSubtasks() != hasSubtasks) {
                return false;
            }
        }
        return true;
    }

    private boolean processBulkOperation(Task task, BulkUpdateRequest request) {
        switch (request.getOperation().toLowerCase()) {
            case "complete":
                return completeTask(task.getId());
                
            case "snooze":
                int days = request.getSnoozeDays() != null ? request.getSnoozeDays() : 1;
                return snoozeTask(task.getId(), LocalDateTime.now().plusDays(days));
                
            case "update_status":
                if (request.getNewStatus() != null) {
                    task.setStatus(request.getNewStatus());
                    taskRepository.save(task);
                    return true;
                }
                break;
                
            case "update_priority":
                if (request.getNewPriority() != null) {
                    task.setPriority(request.getNewPriority());
                    taskRepository.save(task);
                    return true;
                }
                break;
                
            case "update_complexity":
                if (request.getNewComplexity() != null) {
                    task.setComplexity(request.getNewComplexity());
                    taskRepository.save(task);
                    return true;
                }
                break;
                
            case "update_deadline":
                task.setDeadline(request.getNewDeadline());
                taskRepository.save(task);
                return true;
                
            case "delete":
                return deleteTask(task.getId());
                
            default:
                return false;
        }
        return false;
    }

    // Simple stats DTO
    public static class TaskStatsDto {
        private long totalTasks;
        private long completedTasks;
        private long overdueTasks;
        private long urgentTasks;
        private long highPriorityTasks;
        private long mediumPriorityTasks;
        private long lowPriorityTasks;
        private long deadlineTasks;
        private long habitTasks;
        private long reminderTasks;
        private long eventTasks;

        // Getters and setters
        public long getTotalTasks() { return totalTasks; }
        public void setTotalTasks(long totalTasks) { this.totalTasks = totalTasks; }
        public long getCompletedTasks() { return completedTasks; }
        public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }
        public long getOverdueTasks() { return overdueTasks; }
        public void setOverdueTasks(long overdueTasks) { this.overdueTasks = overdueTasks; }
        public long getUrgentTasks() { return urgentTasks; }
        public void setUrgentTasks(long urgentTasks) { this.urgentTasks = urgentTasks; }
        public long getHighPriorityTasks() { return highPriorityTasks; }
        public void setHighPriorityTasks(long highPriorityTasks) { this.highPriorityTasks = highPriorityTasks; }
        public long getMediumPriorityTasks() { return mediumPriorityTasks; }
        public void setMediumPriorityTasks(long mediumPriorityTasks) { this.mediumPriorityTasks = mediumPriorityTasks; }
        public long getLowPriorityTasks() { return lowPriorityTasks; }
        public void setLowPriorityTasks(long lowPriorityTasks) { this.lowPriorityTasks = lowPriorityTasks; }
        public long getDeadlineTasks() { return deadlineTasks; }
        public void setDeadlineTasks(long deadlineTasks) { this.deadlineTasks = deadlineTasks; }
        public long getHabitTasks() { return habitTasks; }
        public void setHabitTasks(long habitTasks) { this.habitTasks = habitTasks; }
        public long getReminderTasks() { return reminderTasks; }
        public void setReminderTasks(long reminderTasks) { this.reminderTasks = reminderTasks; }
        public long getEventTasks() { return eventTasks; }
        public void setEventTasks(long eventTasks) { this.eventTasks = eventTasks; }
    }
}