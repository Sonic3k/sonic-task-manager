package com.sonic.taskmanager.model.dto;

import java.time.LocalDate;

public class TaskFilterDto {
    private String status;
    private String priority;
    private String complexity;
    private String type;
    private LocalDate deadlineFrom;
    private LocalDate deadlineTo;
    private String searchQuery;
    private Boolean hasSubtasks;
    private Boolean isOverdue;
    private Boolean isUrgent;

    public TaskFilterDto() {
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getComplexity() {
        return complexity;
    }

    public void setComplexity(String complexity) {
        this.complexity = complexity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDeadlineFrom() {
        return deadlineFrom;
    }

    public void setDeadlineFrom(LocalDate deadlineFrom) {
        this.deadlineFrom = deadlineFrom;
    }

    public LocalDate getDeadlineTo() {
        return deadlineTo;
    }

    public void setDeadlineTo(LocalDate deadlineTo) {
        this.deadlineTo = deadlineTo;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Boolean getHasSubtasks() {
        return hasSubtasks;
    }

    public void setHasSubtasks(Boolean hasSubtasks) {
        this.hasSubtasks = hasSubtasks;
    }

    public Boolean getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }

    public Boolean getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(Boolean isUrgent) {
        this.isUrgent = isUrgent;
    }
}