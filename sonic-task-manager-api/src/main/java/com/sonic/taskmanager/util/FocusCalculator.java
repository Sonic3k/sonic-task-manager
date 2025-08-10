package com.sonic.taskmanager.util;

import com.sonic.taskmanager.model.Task;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class FocusCalculator {

    /**
     * Determine the best task to focus on today
     * Based on priority, deadline urgency, and complexity
     */
    public Task calculateFocusTask(List<Task> activeTasks) {
        if (activeTasks == null || activeTasks.isEmpty()) {
            return null;
        }

        // Filter to main tasks only (no subtasks)
        List<Task> mainTasks = activeTasks.stream()
                .filter(task -> task.getParentId() == null)
                .filter(task -> !"done".equals(task.getStatus()))
                .toList();

        if (mainTasks.isEmpty()) {
            return null;
        }

        Task bestTask = null;
        double highestScore = -1;

        for (Task task : mainTasks) {
            double score = calculateFocusScore(task);
            if (score > highestScore) {
                highestScore = score;
                bestTask = task;
            }
        }

        return bestTask;
    }

    /**
     * Calculate focus score for a task
     * Higher score = better candidate for focus
     */
    public double calculateFocusScore(Task task) {
        double score = 0;

        // Priority weight (most important factor)
        score += getPriorityWeight(task.getPriority()) * 100;

        // Deadline urgency (very important)
        score += getUrgencyWeight(task) * 80;

        // Complexity bonus (prefer manageable tasks)
        score += getComplexityWeight(task.getComplexity()) * 30;

        // Status bonus (prefer tasks that are already started)
        if ("doing".equals(task.getStatus())) {
            score += 50;
        }

        // Progress bonus (tasks with some progress)
        if (task.getProgressCurrent() != null && task.getProgressCurrent() > 0) {
            score += 20;
        }

        // Penalize snoozed tasks
        if (task.getSnoozedUntil() != null) {
            score -= 100;
        }

        return score;
    }

    /**
     * Get priority weight
     */
    private double getPriorityWeight(String priority) {
        if (priority == null) return 1;
        return switch (priority.toLowerCase()) {
            case "high" -> 3;
            case "medium" -> 2;
            case "low" -> 1;
            default -> 1;
        };
    }

    /**
     * Get urgency weight based on deadline
     */
    private double getUrgencyWeight(Task task) {
        if (task.getDeadline() == null) {
            return 0; // No deadline = not urgent
        }

        LocalDate today = LocalDate.now();
        long daysUntilDeadline = ChronoUnit.DAYS.between(today, task.getDeadline());

        // Overdue tasks get highest urgency
        if (daysUntilDeadline < 0) {
            return 5;
        }

        // Due today
        if (daysUntilDeadline == 0) {
            return 4;
        }

        // Due within 3 days
        if (daysUntilDeadline <= 3) {
            return 3;
        }

        // Due within a week
        if (daysUntilDeadline <= 7) {
            return 2;
        }

        // Due within a month
        if (daysUntilDeadline <= 30) {
            return 1;
        }

        // Far future deadlines
        return 0.5;
    }

    /**
     * Get complexity weight - prefer easier tasks for focus
     */
    private double getComplexityWeight(String complexity) {
        if (complexity == null) return 1;
        return switch (complexity.toLowerCase()) {
            case "easy" -> 2;    // Prefer easy tasks
            case "medium" -> 1.5;
            case "hard" -> 1;    // Don't penalize hard tasks too much
            default -> 1;
        };
    }

    /**
     * Calculate days until deadline for a task
     */
    public Integer calculateDaysUntilDeadline(Task task) {
        if (task.getDeadline() == null) {
            return null;
        }

        LocalDate today = LocalDate.now();
        return (int) ChronoUnit.DAYS.between(today, task.getDeadline());
    }

    /**
     * Determine urgency level for a task
     */
    public String calculateUrgencyLevel(Task task) {
        Integer daysUntil = calculateDaysUntilDeadline(task);
        if (daysUntil == null) {
            return "none";
        }

        if (daysUntil < 0) return "overdue";
        if (daysUntil == 0) return "today";
        if (daysUntil <= 1) return "urgent";
        if (daysUntil <= 3) return "soon";
        if (daysUntil <= 7) return "this_week";
        return "normal";
    }

    /**
     * Generate focus context message for a task
     */
    public String generateFocusContext(Task task) {
        if (task.getFocusContext() != null && !task.getFocusContext().trim().isEmpty()) {
            return task.getFocusContext();
        }

        // Generate default context based on task properties
        StringBuilder context = new StringBuilder();

        if (task.isOverdue()) {
            context.append("This is overdue - let's get it done. ");
        } else if (task.isUrgent()) {
            context.append("Due soon - good time to tackle this. ");
        }

        if ("easy".equals(task.getComplexity())) {
            context.append("Should be quick to finish. ");
        } else if ("hard".equals(task.getComplexity())) {
            context.append("Take your time, just make progress. ");
        }

        if (context.isEmpty()) {
            context.append("Focus on this task today. ");
        }

        return context.toString().trim();
    }
}