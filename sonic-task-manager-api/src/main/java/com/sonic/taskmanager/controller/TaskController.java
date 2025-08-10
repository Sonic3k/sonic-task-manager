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
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Get all active tasks
     */
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

    /**
     * Get task by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse> getTaskById(@PathVariable("id") Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        
        if (task.isPresent()) {
            TaskDto taskDto = taskService.convertToDto(task.get());
            TaskResponse response = new TaskResponse();
            response.setSuccess(true);
            response.setTask(taskDto);
            return ResponseEntity.ok(response);
        } else {
            BaseResponse response = new BaseResponse();
            response.setSuccess(false);
            response.setError("Task not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Create new task
     */
    @PostMapping
    public ResponseEntity<BaseResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        try {
            Task createdTask = taskService.createTask(request);
            TaskDto taskDto = taskService.convertToDto(createdTask);
            
            TaskResponse response = new TaskResponse();
            response.setSuccess(true);
            response.setMessage("Task created successfully");
            response.setTask(taskDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setSuccess(false);
            response.setError("Failed to create task");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Update existing task
     */
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> updateTask(@PathVariable("id") Long id, 
                                                   @Valid @RequestBody CreateTaskRequest request) {
        Optional<Task> updatedTask = taskService.updateTask(id, request);
        
        if (updatedTask.isPresent()) {
            TaskDto taskDto = taskService.convertToDto(updatedTask.get());
            TaskResponse response = new TaskResponse();
            response.setSuccess(true);
            response.setMessage("Task updated successfully");
            response.setTask(taskDto);
            return ResponseEntity.ok(response);
        } else {
            BaseResponse response = new BaseResponse();
            response.setSuccess(false);
            response.setError("Task not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Delete task
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse> deleteTask(@PathVariable("id") Long id) {
        boolean deleted = taskService.deleteTask(id);
        
        BaseResponse response = new BaseResponse();
        if (deleted) {
            response.setSuccess(true);
            response.setMessage("Task deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.setSuccess(false);
            response.setError("Task not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Mark task as completed
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<BaseResponse> completeTask(@PathVariable("id") Long id) {
        boolean completed = taskService.completeTask(id);
        
        BaseResponse response = new BaseResponse();
        if (completed) {
            response.setSuccess(true);
            response.setMessage("Task completed successfully");
            return ResponseEntity.ok(response);
        } else {
            response.setSuccess(false);
            response.setError("Task not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Snooze task
     */
    @PutMapping("/{id}/snooze")
    public ResponseEntity<BaseResponse> snoozeTask(@PathVariable("id") Long id, 
                                                   @RequestParam(required = false, defaultValue = "1") int days) {
        LocalDateTime snoozeUntil = LocalDateTime.now().plusDays(days);
        boolean snoozed = taskService.snoozeTask(id, snoozeUntil);
        
        BaseResponse response = new BaseResponse();
        if (snoozed) {
            response.setSuccess(true);
            response.setMessage("Task snoozed for " + days + " day(s)");
            return ResponseEntity.ok(response);
        } else {
            response.setSuccess(false);
            response.setError("Task not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get subtasks for a parent task
     */
    @GetMapping("/{id}/subtasks")
    public TaskListResponse getSubtasks(@PathVariable("id") Long id) {
        List<Task> subtasks = taskService.getSubtasks(id);
        List<TaskDto> subtaskDtos = subtasks.stream()
                .map(taskService::convertToDto)
                .toList();
        
        TaskListResponse response = new TaskListResponse();
        response.setSuccess(true);
        response.setTasks(subtaskDtos);
        return response;
    }

    /**
     * Get quick win tasks
     */
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

    /**
     * Create subtask for a parent task
     */
    @PostMapping("/{parentId}/subtasks")
    public ResponseEntity<BaseResponse> createSubtask(@PathVariable("parentId") Long parentId,
                                                      @Valid @RequestBody CreateTaskRequest request) {
        // Set the parent ID
        request.setParentId(parentId);
        
        try {
            Task createdSubtask = taskService.createTask(request);
            TaskDto taskDto = taskService.convertToDto(createdSubtask);
            
            TaskResponse response = new TaskResponse();
            response.setSuccess(true);
            response.setMessage("Subtask created successfully");
            response.setTask(taskDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setSuccess(false);
            response.setError("Failed to create subtask");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Complete multiple tasks at once
     */
    @PutMapping("/complete-multiple")
    public ResponseEntity<BaseResponse> completeMultipleTasks(@RequestBody List<Long> taskIds) {
        try {
            int completedCount = 0;
            for (Long taskId : taskIds) {
                if (taskService.completeTask(taskId)) {
                    completedCount++;
                }
            }
            
            BaseResponse response = new BaseResponse();
            response.setSuccess(true);
            response.setMessage("Completed " + completedCount + " out of " + taskIds.size() + " tasks");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setSuccess(false);
            response.setError("Failed to complete tasks");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Error handling
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> handleIllegalArgument(IllegalArgumentException e) {
        BaseResponse response = new BaseResponse();
        response.setSuccess(false);
        response.setError("Invalid request");
        response.setMessage(e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGenericException(Exception e) {
        BaseResponse response = new BaseResponse();
        response.setSuccess(false);
        response.setError("Internal server error");
        response.setMessage("An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}