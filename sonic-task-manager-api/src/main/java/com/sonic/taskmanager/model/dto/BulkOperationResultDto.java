package com.sonic.taskmanager.model.dto;

import java.util.List;
import java.util.ArrayList;

public class BulkOperationResultDto {
    private int totalRequested;
    private int successCount;
    private int failureCount;
    private List<String> errors;
    private String operation;
    private List<Long> processedTaskIds;

    public BulkOperationResultDto() {
        this.errors = new ArrayList<>();
        this.processedTaskIds = new ArrayList<>();
    }

    public BulkOperationResultDto(String operation, int totalRequested) {
        this();
        this.operation = operation;
        this.totalRequested = totalRequested;
    }

    // Getters and Setters
    public int getTotalRequested() {
        return totalRequested;
    }

    public void setTotalRequested(int totalRequested) {
        this.totalRequested = totalRequested;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public List<Long> getProcessedTaskIds() {
        return processedTaskIds;
    }

    public void setProcessedTaskIds(List<Long> processedTaskIds) {
        this.processedTaskIds = processedTaskIds;
    }

    // Helper methods
    public void addError(String error) {
        this.errors.add(error);
        this.failureCount++;
    }

    public void addSuccess(Long taskId) {
        this.processedTaskIds.add(taskId);
        this.successCount++;
    }

    public boolean isCompleteSuccess() {
        return failureCount == 0;
    }

    public double getSuccessRate() {
        if (totalRequested == 0) return 0.0;
        return (double) successCount / totalRequested * 100;
    }
}