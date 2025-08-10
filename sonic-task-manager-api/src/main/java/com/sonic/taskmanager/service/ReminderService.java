package com.sonic.taskmanager.service;

import com.sonic.taskmanager.model.Task;
import com.sonic.taskmanager.repository.TaskRepository;
import com.sonic.taskmanager.util.DateUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReminderService {

    private final TaskRepository taskRepository;

    public ReminderService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Get reminders that should be shown today
     * Based on frequency and last shown time
     */
    public List<Task> getActiveReminders() {
        LocalDateTime now = LocalDateTime.now();
        
        // Reminders not touched for 3 days should surface
        LocalDateTime reminderThreshold = now.minusDays(3);

        List<Task> reminders = taskRepository.findActiveReminders(now, reminderThreshold);
        
        // Apply some randomness to avoid showing too many at once
        return reminders.stream()
                .filter(this::shouldShowReminderToday)
                .limit(3) // Max 3 reminders at once
                .toList();
    }

    /**
     * Determine if a reminder should be shown today
     * Add some randomness to make it feel natural
     */
    private boolean shouldShowReminderToday(Task reminder) {
        // Always show if it's been more than a week
        if (reminder.getUpdatedAt().isBefore(LocalDateTime.now().minusWeeks(1))) {
            return true;
        }

        // For gentle reminders, show with some probability based on how long it's been
        long daysSinceUpdate = DateUtils.daysBetween(
            reminder.getUpdatedAt().toLocalDate(), 
            DateUtils.today()
        );

        // Probability increases with time
        double probability = Math.min(0.7, daysSinceUpdate * 0.15);
        
        // Use task ID to create consistent randomness (same task gets same probability each day)
        int seed = (int) (reminder.getId() % 100);
        return (seed / 100.0) < probability;
    }

    /**
     * Snooze a reminder for specified days
     */
    @Transactional
    public void snoozeReminder(Long taskId, int days) {
        taskRepository.findById(taskId).ifPresent(task -> {
            if ("reminder".equals(task.getType())) {
                task.setSnoozedUntil(LocalDateTime.now().plusDays(days));
                taskRepository.save(task);
            }
        });
    }

    /**
     * Mark reminder as acknowledged (updates timestamp)
     */
    @Transactional  
    public void acknowledgeReminder(Long taskId) {
        taskRepository.findById(taskId).ifPresent(task -> {
            if ("reminder".equals(task.getType())) {
                // Just updating will trigger @PreUpdate and update the updatedAt timestamp
                task.setUpdatedAt(LocalDateTime.now());
                taskRepository.save(task);
            }
        });
    }
}
