package com.sonic.taskmanager.model.response;

import java.util.List;

import com.sonic.taskmanager.model.dto.PaginationDto;
import com.sonic.taskmanager.model.dto.TaskDto;

public class PaginatedTaskResponse extends BaseResponse {
    private List<TaskDto> tasks;
    private PaginationDto pagination;

    public PaginatedTaskResponse() {
        super();
    }

    public List<TaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDto> tasks) {
        this.tasks = tasks;
    }

    public PaginationDto getPagination() {
        return pagination;
    }

    public void setPagination(PaginationDto pagination) {
        this.pagination = pagination;
    }
}