package com.sonic.taskmanager.model.dto;

import com.sonic.taskmanager.model.Task;
import java.util.List;

public class WorkspaceDto {

    private String dailyMood;
    private Task focusTask;
    private List<Task> nextUpStack;
    private List<Task> activeReminders;
    private List<Task> quickWins;
    private ShowSectionsDto showSections;
    private WorkloadAssessmentDto workloadAssessment;

    public WorkspaceDto() {
    }

    // Getters and Setters
    public String getDailyMood() {
        return dailyMood;
    }

    public void setDailyMood(String dailyMood) {
        this.dailyMood = dailyMood;
    }

    public Task getFocusTask() {
        return focusTask;
    }

    public void setFocusTask(Task focusTask) {
        this.focusTask = focusTask;
    }

    public List<Task> getNextUpStack() {
        return nextUpStack;
    }

    public void setNextUpStack(List<Task> nextUpStack) {
        this.nextUpStack = nextUpStack;
    }

    public List<Task> getActiveReminders() {
        return activeReminders;
    }

    public void setActiveReminders(List<Task> activeReminders) {
        this.activeReminders = activeReminders;
    }

    public List<Task> getQuickWins() {
        return quickWins;
    }

    public void setQuickWins(List<Task> quickWins) {
        this.quickWins = quickWins;
    }

    public ShowSectionsDto getShowSections() {
        return showSections;
    }

    public void setShowSections(ShowSectionsDto showSections) {
        this.showSections = showSections;
    }

    public WorkloadAssessmentDto getWorkloadAssessment() {
        return workloadAssessment;
    }

    public void setWorkloadAssessment(WorkloadAssessmentDto workloadAssessment) {
        this.workloadAssessment = workloadAssessment;
    }

    // Nested DTOs
    public static class ShowSectionsDto {
        private boolean urgentTasks;
        private boolean quickWins;
        private boolean habits;
        private boolean reminders;

        public ShowSectionsDto() {
        }

        // Getters and Setters
        public boolean isUrgentTasks() {
            return urgentTasks;
        }

        public void setUrgentTasks(boolean urgentTasks) {
            this.urgentTasks = urgentTasks;
        }

        public boolean isQuickWins() {
            return quickWins;
        }

        public void setQuickWins(boolean quickWins) {
            this.quickWins = quickWins;
        }

        public boolean isHabits() {
            return habits;
        }

        public void setHabits(boolean habits) {
            this.habits = habits;
        }

        public boolean isReminders() {
            return reminders;
        }

        public void setReminders(boolean reminders) {
            this.reminders = reminders;
        }
    }

    public static class WorkloadAssessmentDto {
        private int totalTasks;
        private int urgentCount;
        private double estimatedHours;
        private String recommendation;

        public WorkloadAssessmentDto() {
        }

        // Getters and Setters
        public int getTotalTasks() {
            return totalTasks;
        }

        public void setTotalTasks(int totalTasks) {
            this.totalTasks = totalTasks;
        }

        public int getUrgentCount() {
            return urgentCount;
        }

        public void setUrgentCount(int urgentCount) {
            this.urgentCount = urgentCount;
        }

        public double getEstimatedHours() {
            return estimatedHours;
        }

        public void setEstimatedHours(double estimatedHours) {
            this.estimatedHours = estimatedHours;
        }

        public String getRecommendation() {
            return recommendation;
        }

        public void setRecommendation(String recommendation) {
            this.recommendation = recommendation;
        }
    }
}