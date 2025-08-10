package com.sonic.taskmanager.controller;

import com.sonic.taskmanager.model.dto.TaskFilterDto;
import com.sonic.taskmanager.model.request.BulkUpdateRequest;
import com.sonic.taskmanager.model.response.BaseResponse;
import com.sonic.taskmanager.model.response.BulkOperationResponse;
import com.sonic.taskmanager.model.response.PaginatedTaskResponse;
import com.sonic.taskmanager.service.TaskService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/studio")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class StudioController {

    private final TaskService taskService;

    public StudioController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public PaginatedTaskResponse getTasksPaginated(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir,
            
            // Filter parameters
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "priority", required = false) String priority,
            @RequestParam(name = "complexity", required = false) String complexity,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "deadlineFrom", required = false) String deadlineFrom,
            @RequestParam(name = "deadlineTo", required = false) String deadlineTo,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "hasSubtasks", required = false) Boolean hasSubtasks,
            @RequestParam(name = "isOverdue", required = false) Boolean isOverdue,
            @RequestParam(name = "isUrgent", required = false) Boolean isUrgent) {

        // Validate pagination parameters
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        // Create sort object
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Build filter DTO
        TaskFilterDto filter = new TaskFilterDto();
        filter.setStatus(status);
        filter.setPriority(priority);
        filter.setComplexity(complexity);
        filter.setType(type);
        filter.setSearchQuery(search);
        filter.setHasSubtasks(hasSubtasks);
        filter.setIsOverdue(isOverdue);
        filter.setIsUrgent(isUrgent);

        // Parse date filters
        if (deadlineFrom != null && !deadlineFrom.trim().isEmpty()) {
            try {
                filter.setDeadlineFrom(java.time.LocalDate.parse(deadlineFrom));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid deadlineFrom format. Use YYYY-MM-DD");
            }
        }
        if (deadlineTo != null && !deadlineTo.trim().isEmpty()) {
            try {
                filter.setDeadlineTo(java.time.LocalDate.parse(deadlineTo));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid deadlineTo format. Use YYYY-MM-DD");
            }
        }

        return taskService.getTasksPaginated(pageable, filter);
    }

    @PostMapping("/tasks/bulk-update")
    public BulkOperationResponse bulkUpdateTasks(@RequestBody BulkUpdateRequest request) {
        // Validate request
        if (request.getTaskIds() == null || request.getTaskIds().isEmpty()) {
            throw new IllegalArgumentException("No task IDs provided");
        }

        if (request.getOperation() == null || request.getOperation().trim().isEmpty()) {
            throw new IllegalArgumentException("No operation specified");
        }

        // Validate operation type
        String operation = request.getOperation().toLowerCase();
        if (!operation.matches("complete|snooze|update_status|update_priority|update_complexity|delete")) {
            throw new IllegalArgumentException("Invalid operation: " + request.getOperation());
        }

        var result = taskService.bulkUpdateTasks(request);
        
        BulkOperationResponse response = new BulkOperationResponse();
        response.setSuccess(result.isCompleteSuccess());
        response.setResult(result);
        
        if (result.isCompleteSuccess()) {
            response.setMessage("All tasks processed successfully");
        } else {
            response.setMessage(String.format("Processed %d/%d tasks successfully", 
                result.getSuccessCount(), result.getTotalRequested()));
        }

        return response;
    }

    @GetMapping("/stats")
    public BaseResponse getTaskStatistics() {
        var stats = taskService.getTaskStatistics();
        
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Statistics retrieved successfully");
        
        return response;
    }

    @PostMapping("/tasks/search")
    public PaginatedTaskResponse searchTasks(
            @RequestBody TaskFilterDto filter,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

        // Validate pagination parameters
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return taskService.getTasksPaginated(pageable, filter);
    }

    @GetMapping("/health")
    public BaseResponse healthCheck() {
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Studio API is healthy");
        return response;
    }
}