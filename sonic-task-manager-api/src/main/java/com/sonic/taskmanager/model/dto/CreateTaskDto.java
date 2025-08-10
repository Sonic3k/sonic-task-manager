package com.sonic.taskmanager.model.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class CreateTaskDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String type = "deadline"; // default to deadline
    private String priority = "medium"; // default to medium
    private String complexity = "medium"; // default to medium
    private Long parentId;
    private LocalDate deadline;
    private LocalDate scheduledDate;
    private String focusContext;
    private String tags;
    private String context;

    public CreateTaskDto() {
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getFocusContext() {
        return focusContext;
    }

    public void setFocusContext(String focusContext) {
        this.focusContext = focusContext;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}