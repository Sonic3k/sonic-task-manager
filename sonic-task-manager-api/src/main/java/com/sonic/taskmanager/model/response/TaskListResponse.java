package com.sonic.taskmanager.model.response;

import java.util.List;

import com.sonic.taskmanager.model.dto.TaskDto;

public class TaskListResponse extends BaseResponse {
    private List<TaskDto> tasks;

    public TaskListResponse() {
        super();
    }

    public List<TaskDto> getTasks() { return tasks; }
    public void setTasks(List<TaskDto> tasks) { this.tasks = tasks; }
}
