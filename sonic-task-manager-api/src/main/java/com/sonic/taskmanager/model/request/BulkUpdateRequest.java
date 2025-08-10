package com.sonic.taskmanager.model.request;

import java.time.LocalDate;
import java.util.List;

public class BulkUpdateRequest {
    private List<Long> taskIds;
    private String operation; // "complete", "snooze", "update_priority", "update_status", "delete"
    
    // For snooze operation
    private Integer snoozeDays;
    
    // For update operations
    private String newStatus;
    private String newPriority;
    private String newComplexity;
    private LocalDate newDeadline;
    
    public BulkUpdateRequest() {
    }

    // Getters and Setters
    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Integer getSnoozeDays() {
        return snoozeDays;
    }

    public void setSnoozeDays(Integer snoozeDays) {
        this.snoozeDays = snoozeDays;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getNewPriority() {
        return newPriority;
    }

    public void setNewPriority(String newPriority) {
        this.newPriority = newPriority;
    }

    public String getNewComplexity() {
        return newComplexity;
    }

    public void setNewComplexity(String newComplexity) {
        this.newComplexity = newComplexity;
    }

    public LocalDate getNewDeadline() {
        return newDeadline;
    }

    public void setNewDeadline(LocalDate newDeadline) {
        this.newDeadline = newDeadline;
    }
}