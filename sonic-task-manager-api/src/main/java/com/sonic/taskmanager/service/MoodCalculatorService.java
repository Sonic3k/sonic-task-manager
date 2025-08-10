package com.sonic.taskmanager.service;

import com.sonic.taskmanager.model.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MoodCalculatorService {

    /**
     * Calculate daily mood based on current workload
     */
    public String calculateDailyMood(List<Task> activeTasks) {
        if (activeTasks == null || activeTasks.isEmpty()) {
            return "relaxed";
        }

        // Filter to main tasks only
        List<Task> mainTasks = activeTasks.stream()
                .filter(task -> task.getParentId() == null)
                .filter(task -> !"done".equals(task.getStatus()))
                .toList();

        int totalTasks = mainTasks.size();
        
        // Count different types of pressure
        long overdueCount = mainTasks.stream().filter(Task::isOverdue).count();
        long urgentCount = mainTasks.stream().filter(Task::isUrgent).count();
        long highPriorityCount = mainTasks.stream()
                .filter(task -> "high".equals(task.getPriority()))
                .count();
        long hardTasksCount = mainTasks.stream()
                .filter(task -> "hard".equals(task.getComplexity()))
                .count();

        // Calculate stress level
        double stressScore = 0;
        stressScore += overdueCount * 3; // Overdue is most stressful
        stressScore += urgentCount * 2;  // Urgent is also stressful
        stressScore += highPriorityCount * 1.5; // High priority adds stress
        stressScore += hardTasksCount * 1; // Hard tasks add some stress
        stressScore += totalTasks * 0.5; // Each task adds a little stress

        // Normalize stress score
        double normalizedStress = stressScore / Math.max(totalTasks, 1);

        // Determine mood based on stress level
        if (normalizedStress >= 4) {
            return "intense"; // Very stressful day
        } else if (normalizedStress >= 3) {
            return "busy"; // Busy day with pressure
        } else if (normalizedStress >= 2) {
            return "active"; // Active day with some pressure
        } else if (normalizedStress >= 1) {
            return "steady"; // Steady workload
        } else {
            return "chill"; // Light workload
        }
    }

    /**
     * Get mood description for display
     */
    public String getMoodDescription(String mood) {
        return switch (mood) {
            case "intense" -> "Intense day ahead - stay focused";
            case "busy" -> "Busy day with important tasks";
            case "active" -> "Active day with good momentum";
            case "steady" -> "Steady progress day";
            case "chill" -> "Light day - good for deep work";
            case "relaxed" -> "Relaxed day - catch up or plan ahead";
            default -> "Ready for the day";
        };
    }

    /**
     * Get mood emoji for visual feedback
     */
    public String getMoodEmoji(String mood) {
        return switch (mood) {
            case "intense" -> "🔥";
            case "busy" -> "⚡";
            case "active" -> "🎯";
            case "steady" -> "✊";
            case "chill" -> "🌤️";
            case "relaxed" -> "😌";
            default -> "👍";
        };
    }
}