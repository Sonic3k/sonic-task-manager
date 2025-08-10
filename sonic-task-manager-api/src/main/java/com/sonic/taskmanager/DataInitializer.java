package com.sonic.taskmanager;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.sonic.taskmanager.model.HabitSession;
import com.sonic.taskmanager.model.Preferences;
import com.sonic.taskmanager.model.Task;
import com.sonic.taskmanager.repository.HabitSessionRepository;
import com.sonic.taskmanager.repository.PreferencesRepository;
import com.sonic.taskmanager.repository.TaskRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TaskRepository taskRepository;
    private final HabitSessionRepository habitSessionRepository;
    private final PreferencesRepository preferencesRepository;

    public DataInitializer(TaskRepository taskRepository, 
                          HabitSessionRepository habitSessionRepository,
                          PreferencesRepository preferencesRepository) {
        this.taskRepository = taskRepository;
        this.habitSessionRepository = habitSessionRepository;
        this.preferencesRepository = preferencesRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (taskRepository.count() == 0) {
            System.out.println("Initializing sample data...");
            createSampleTasks();
            createSampleHabitSessions();
            createDefaultPreferences();
            System.out.println("Sample data created successfully!");
        } else {
            System.out.println("Database already contains data. Skipping initialization.");
        }
    }

    private void createSampleTasks() {
        // 1. FOCUS TASK CANDIDATE - Urgent deadline task
        Task photovista = createTask(
            "Complete Photovista wireframes",
            "Design the main user interface wireframes for the photo sharing app",
            "deadline", "high", "hard",
            LocalDate.now().plusDays(3), // Due in 3 days - should become focus
            "Take your time with the design, just get the main flow sketched out"
        );
        Task photovistaId = taskRepository.save(photovista);

        // Add subtasks for Photovista
        createSubtask(photovistaId.getId(), "Research competitor apps", "done");
        createSubtask(photovistaId.getId(), "Sketch main screens", "todo");
        createSubtask(photovistaId.getId(), "Create user flow", "todo");
        createSubtask(photovistaId.getId(), "Review with team", "todo");

        // 2. OVERDUE TASK - Should become highest focus
        Task reactNative = createTask(
            "Setup React Native development environment",
            "Install and configure React Native for mobile development",
            "deadline", "high", "hard",
            LocalDate.now().minusDays(2), // Overdue by 2 days
            "This is overdue - let's get it done step by step"
        );
        Task reactNativeId = taskRepository.save(reactNative);

        createSubtask(reactNativeId.getId(), "Install Node.js and npm", "todo");
        createSubtask(reactNativeId.getId(), "Install React Native CLI", "todo");
        createSubtask(reactNativeId.getId(), "Setup Android Studio", "todo");

        // 3. NEXT UP STACK TASKS - Various priorities
        taskRepository.save(createTask(
            "Reply to client emails",
            "Respond to 3 pending client emails from this week",
            "deadline", "high", "easy",
            LocalDate.now().plusDays(1),
            null
        ));

        taskRepository.save(createTask(
            "Update portfolio website",
            "Add recent projects and update contact information",
            "deadline", "medium", "medium",
            LocalDate.now().plusDays(7),
            null
        ));

        taskRepository.save(createTask(
            "Research laptop options",
            "Compare different laptop models for development work",
            "deadline", "medium", "easy",
            LocalDate.now().plusDays(14),
            null
        ));

        taskRepository.save(createTask(
            "Plan weekend trip to Da Lat",
            "Book hotel and plan itinerary for weekend getaway",
            "deadline", "low", "medium",
            LocalDate.now().plusDays(10),
            null
        ));

        // 4. QUICK WINS - High priority + Easy complexity
        taskRepository.save(createTask(
            "Backup project files to cloud",
            "Upload important project files to Google Drive",
            "deadline", "high", "easy",
            LocalDate.now().plusDays(5),
            null
        ));

        taskRepository.save(createTask(
            "Call dentist for appointment",
            "Schedule dental checkup for next month",
            "deadline", "high", "easy",
            LocalDate.now().plusDays(2),
            null
        ));

        taskRepository.save(createTask(
            "Order new keyboard",
            "Replace broken mechanical keyboard for better typing",
            "deadline", "high", "easy",
            LocalDate.now().plusDays(7),
            null
        ));

        // 5. HABIT/LEARNING TASKS - Long term, no pressure
        Task pianoTask = createTask(
            "Practice piano - Für Elise",
            "Continue learning classical piano piece",
            "habit", "medium", "medium",
            null,
            "Take your time, practice when you feel like it"
        );
        Task pianoId = taskRepository.save(pianoTask);

        Task englishTask = createTask(
            "Improve English speaking",
            "Practice English conversation through online sessions",
            "habit", "medium", "medium",
            null,
            "15-20 minutes daily practice is enough"
        );
        taskRepository.save(englishTask);

        Task readingTask = createTask(
            "Read 'Clean Code' book",
            "Finish reading the software development classic",
            "habit", "low", "medium",
            null,
            "Read one chapter at a time, no rush"
        );
        taskRepository.save(readingTask);

        // 6. GENTLE REMINDERS - Should surface occasionally
        taskRepository.save(createTask(
            "Think about buying a car",
            "Consider whether it's time to purchase a vehicle",
            "reminder", "low", "hard",
            null,
            null
        ));

        taskRepository.save(createTask(
            "Plan mother's birthday party",
            "Start thinking about celebration ideas for next month",
            "reminder", "medium", "medium",
            LocalDate.now().plusDays(45),
            null
        ));

        taskRepository.save(createTask(
            "Research investment options",
            "Look into different ways to invest savings",
            "reminder", "low", "hard",
            null,
            null
        ));

        // 7. UPCOMING EVENTS - Time-sensitive
        taskRepository.save(createTask(
            "Team standup meeting",
            "Weekly team sync and progress updates",
            "event", "medium", "easy",
            LocalDate.now(),
            null
        ));

        taskRepository.save(createTask(
            "Visit grandmother for weekend",
            "Family visit planned for this weekend",
            "event", "high", "easy",
            LocalDate.now().plusDays(2),
            null
        ));

        // 8. COMPLETED TASKS - For testing completed state
        Task completedTask = createTask(
            "Set up development environment",
            "Install necessary development tools",
            "deadline", "high", "medium",
            LocalDate.now().minusDays(1),
            null
        );
        completedTask.setStatus("done");
        completedTask.setCompletedAt(LocalDateTime.now().minusHours(2));
        completedTask.setProgressCurrent(1);
        taskRepository.save(completedTask);

        Task completedTask2 = createTask(
            "Buy groceries for the week",
            "Weekly grocery shopping",
            "deadline", "medium", "easy",
            LocalDate.now().minusDays(1),
            null
        );
        completedTask2.setStatus("done");
        completedTask2.setCompletedAt(LocalDateTime.now().minusHours(5));
        completedTask2.setProgressCurrent(1);
        taskRepository.save(completedTask2);

        // Update progress for parent tasks with subtasks
        updateParentTaskProgress();
    }

    private Task createTask(String title, String description, String type, String priority, 
                           String complexity, LocalDate deadline, String focusContext) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setType(type);
        task.setPriority(priority);
        task.setComplexity(complexity);
        task.setDeadline(deadline);
        task.setFocusContext(focusContext);
        task.setStatus("todo");
        task.setProgressCurrent(0);
        task.setProgressTotal(1);
        return task;
    }

    private void createSubtask(Long parentId, String title, String status) {
        Task subtask = new Task();
        subtask.setTitle(title);
        subtask.setParentId(parentId);
        subtask.setType("deadline");
        subtask.setPriority("medium");
        subtask.setComplexity("easy");
        subtask.setStatus(status);
        subtask.setProgressCurrent(status.equals("done") ? 1 : 0);
        subtask.setProgressTotal(1);
        if (status.equals("done")) {
            subtask.setCompletedAt(LocalDateTime.now().minusHours(1));
        }
        taskRepository.save(subtask);
    }

    private void createSampleHabitSessions() {
        // Find piano task to add some habit sessions
        taskRepository.findByTitle("Practice piano - Für Elise").ifPresent(pianoTask -> {
            // Add some practice sessions from past week
            for (int i = 7; i >= 1; i--) {
                HabitSession session = new HabitSession();
                session.setTaskId(pianoTask.getId());
                session.setSessionDate(LocalDate.now().minusDays(i));
                session.setDurationMinutes(20 + (i * 5)); // Varying duration
                session.setProgressNote("Practiced for " + (20 + i * 5) + " minutes - getting better!");
                habitSessionRepository.save(session);
            }
        });

        // Add English practice sessions
        taskRepository.findByTitle("Improve English speaking").ifPresent(englishTask -> {
            for (int i = 5; i >= 1; i--) {
                HabitSession session = new HabitSession();
                session.setTaskId(englishTask.getId());
                session.setSessionDate(LocalDate.now().minusDays(i));
                session.setDurationMinutes(15);
                session.setProgressNote("Conversation practice - " + (i == 1 ? "great" : "good") + " session");
                habitSessionRepository.save(session);
            }
        });
    }

    private void createDefaultPreferences() {
        createPreference("daily_mood", "chill");
        createPreference("work_hours_start", "09:00");
        createPreference("work_hours_end", "17:00");
        createPreference("focus_session_duration", "90");
        createPreference("gentle_reminder_frequency", "3");
        createPreference("show_completed_tasks", "true");
        createPreference("workspace_theme", "warm");
    }

    private void createPreference(String key, String value) {
        Preferences pref = new Preferences();
        pref.setKey(key);
        pref.setValue(value);
        preferencesRepository.save(pref);
    }

    private void updateParentTaskProgress() {
        // Update Photovista progress (1 of 4 subtasks done)
        taskRepository.findByTitle("Complete Photovista wireframes").ifPresent(parent -> {
            long completedSubtasks = taskRepository.findByParentId(parent.getId()).stream()
                    .filter(task -> "done".equals(task.getStatus()))
                    .count();
            long totalSubtasks = taskRepository.findByParentId(parent.getId()).size();
            
            parent.setProgressCurrent((int) completedSubtasks);
            parent.setProgressTotal((int) totalSubtasks);
            
            if (completedSubtasks > 0) {
                parent.setStatus("doing"); // Some progress made
            }
            
            taskRepository.save(parent);
        });

        // React Native task - no subtasks completed yet
        taskRepository.findByTitle("Setup React Native development environment").ifPresent(parent -> {
            long totalSubtasks = taskRepository.findByParentId(parent.getId()).size();
            parent.setProgressCurrent(0);
            parent.setProgressTotal((int) totalSubtasks);
            taskRepository.save(parent);
        });
    }
}