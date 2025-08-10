package com.sonic.taskmanager.model.response;

import com.sonic.taskmanager.model.dto.TaskDto;

public class TaskResponse extends BaseResponse {
    private TaskDto task;

    public TaskResponse() {
        super();
    }

    public TaskDto getTask() { return task; }
    public void setTask(TaskDto task) { this.task = task; }
}