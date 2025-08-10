package com.sonic.taskmanager.controller;

import com.sonic.taskmanager.model.Task;
import com.sonic.taskmanager.model.dto.TaskDto;
import com.sonic.taskmanager.model.request.CreateTaskRequest;
import com.sonic.taskmanager.model.response.BaseResponse;
import com.sonic.taskmanager.model.response.TaskResponse;
import com.sonic.taskmanager.model.response.TaskListResponse;
import com.sonic.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public TaskListResponse getAllTasks() {
        List<Task> tasks = taskService.getAllActiveTasks();
        List<TaskDto> taskDtos = tasks.stream()
                .map(taskService::convertToDto)
                .toList();
        
        TaskListResponse response = new TaskListResponse();
        response.setSuccess(true);
        response.setTasks(taskDtos);
        return response;
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable("id") Long id) {
        Task task = taskService.getTaskById(id)
                .orElseThrow(() -> new NoSuchElementException("Task with ID " + id + " not found"));
        
        TaskDto taskDto = taskService.convertToDto(task);
        TaskResponse response = new TaskResponse();
        response.setSuccess(true);
        response.setTask(taskDto);
        return response;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        Task createdTask = taskService.createTask(request);
        TaskDto taskDto = taskService.convertToDto(createdTask);
        
        TaskResponse response = new TaskResponse();
        response.setSuccess(true);
        response.setMessage("Task created successfully");
        response.setTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable("id") Long id, 
                                   @Valid @RequestBody CreateTaskRequest request) {
        Task updatedTask = taskService.updateTask(id, request)
                .orElseThrow(() -> new NoSuchElementException("Task with ID " + id + " not found"));
        
        TaskDto taskDto = taskService.convertToDto(updatedTask);
        TaskResponse response = new TaskResponse();
        response.setSuccess(true);
        response.setMessage("Task updated successfully");
        response.setTask(taskDto);
        return response;
    }

    @DeleteMapping("/{id}")
    public BaseResponse deleteTask(@PathVariable("id") Long id) {
        boolean deleted = taskService.deleteTask(id);
        if (!deleted) {
            throw new NoSuchElementException("Task with ID " + id + " not found");
        }
        
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Task deleted successfully");
        return response;
    }

    @PutMapping("/{id}/complete")
    public BaseResponse completeTask(@PathVariable("id") Long id) {
        boolean completed = taskService.completeTask(id);
        if (!completed) {
            throw new NoSuchElementException("Task with ID " + id + " not found");
        }
        
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Task completed successfully");
        return response;
    }

    @PutMapping("/{id}/snooze")
    public BaseResponse snoozeTask(@PathVariable("id") Long id, 
                                   @RequestParam(name = "days", required = false, defaultValue = "1") int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Days must be greater than 0");
        }
        
        LocalDateTime snoozeUntil = LocalDateTime.now().plusDays(days);
        boolean snoozed = taskService.snoozeTask(id, snoozeUntil);
        if (!snoozed) {
            throw new NoSuchElementException("Task with ID " + id + " not found");
        }
        
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Task snoozed for " + days + " day(s)");
        return response;
    }

    @GetMapping("/{id}/subtasks")
    public TaskListResponse getSubtasks(@PathVariable("id") Long id) {
        // Verify parent task exists first
        taskService.getTaskById(id)
                .orElseThrow(() -> new NoSuchElementException("Parent task with ID " + id + " not found"));
        
        List<Task> subtasks = taskService.getSubtasks(id);
        List<TaskDto> subtaskDtos = subtasks.stream()
                .map(taskService::convertToDto)
                .toList();
        
        TaskListResponse response = new TaskListResponse();
        response.setSuccess(true);
        response.setTasks(subtaskDtos);
        return response;
    }

    @GetMapping("/quick-wins")
    public TaskListResponse getQuickWins() {
        List<Task> quickWins = taskService.findQuickWinTasks();
        List<TaskDto> quickWinDtos = quickWins.stream()
                .map(taskService::convertToDto)
                .toList();
        
        TaskListResponse response = new TaskListResponse();
        response.setSuccess(true);
        response.setTasks(quickWinDtos);
        return response;
    }

    @PostMapping("/{parentId}/subtasks")
    public ResponseEntity<TaskResponse> createSubtask(@PathVariable("parentId") Long parentId,
                                                      @Valid @RequestBody CreateTaskRequest request) {
        // Verify parent task exists
        taskService.getTaskById(parentId)
                .orElseThrow(() -> new NoSuchElementException("Parent task with ID " + parentId + " not found"));
        
        request.setParentId(parentId);
        Task createdSubtask = taskService.createTask(request);
        TaskDto taskDto = taskService.convertToDto(createdSubtask);
        
        TaskResponse response = new TaskResponse();
        response.setSuccess(true);
        response.setMessage("Subtask created successfully");
        response.setTask(taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/complete-multiple")
    public BaseResponse completeMultipleTasks(@RequestBody List<Long> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            throw new IllegalArgumentException("Task IDs list cannot be empty");
        }
        
        int completedCount = 0;
        for (Long taskId : taskIds) {
            if (taskService.completeTask(taskId)) {
                completedCount++;
            }
        }
        
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Completed " + completedCount + " out of " + taskIds.size() + " tasks");
        return response;
    }
}