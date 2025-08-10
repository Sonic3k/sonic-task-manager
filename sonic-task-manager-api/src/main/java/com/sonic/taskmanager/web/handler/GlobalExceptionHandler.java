package com.sonic.taskmanager.web.handler;

import com.sonic.taskmanager.model.response.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Simple Global Exception Handler
 * Just catch standard exceptions and return BaseResponse
 * All response types extend BaseResponse anyway
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle validation errors from @Valid
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        logger.warn("Validation failed: {}", ex.getMessage());
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        BaseResponse response = new BaseResponse();
        response.setSuccess(false);
        response.setError("Validation failed");
        response.setMessage("Invalid input: " + fieldErrors.toString());
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle "not found" cases
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<BaseResponse> handleNotFound(NoSuchElementException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        
        BaseResponse response = new BaseResponse();
        response.setSuccess(false);
        response.setError("Resource not found");
        response.setMessage(ex.getMessage() != null ? ex.getMessage() : "Requested resource not found");
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handle bad request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> handleBadRequest(IllegalArgumentException ex) {
        logger.warn("Bad request: {}", ex.getMessage());
        
        BaseResponse response = new BaseResponse();
        response.setSuccess(false);
        response.setError("Invalid request");
        response.setMessage(ex.getMessage());
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle database errors
     */
    @ExceptionHandler({
        org.springframework.dao.DataAccessException.class,
        org.springframework.transaction.TransactionException.class
    })
    public ResponseEntity<BaseResponse> handleDatabaseError(Exception ex) {
        logger.error("Database error: {}", ex.getMessage(), ex);
        
        BaseResponse response = new BaseResponse();
        response.setSuccess(false);
        response.setError("Database error");
        response.setMessage("A database error occurred. Please try again.");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handle all other exceptions
     * Special case: Workspace endpoints return empty workspace instead of error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGenericError(Exception ex, 
                                                          HttpServletRequest request) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        // Special handling for workspace endpoints
        String path = request.getRequestURI();
        if (path.contains("/api/workspace")) {
            com.sonic.taskmanager.model.response.WorkspaceResponse response = 
                new com.sonic.taskmanager.model.response.WorkspaceResponse();
            response.setSuccess(true);
            response.setMessage("Workspace loaded with default data");
            response.setWorkspace(createEmptyWorkspace());
            return ResponseEntity.ok(response);
        }
        
        // Default error response for other endpoints
        BaseResponse response = new BaseResponse();
        response.setSuccess(false);
        response.setError("Internal server error");
        response.setMessage("An unexpected error occurred");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * Create empty workspace for fallback
     */
    private com.sonic.taskmanager.model.dto.WorkspaceDto createEmptyWorkspace() {
        com.sonic.taskmanager.model.dto.WorkspaceDto workspace = 
            new com.sonic.taskmanager.model.dto.WorkspaceDto();
        workspace.setFocusTask(null);
        workspace.setNextUpStack(java.util.Collections.emptyList());
        workspace.setQuickWins(java.util.Collections.emptyList());
        workspace.setActiveReminders(java.util.Collections.emptyList());
        workspace.setDailyMood("relaxed");

        // Empty show sections
        com.sonic.taskmanager.model.dto.WorkspaceDto.ShowSectionsDto showSections = 
            new com.sonic.taskmanager.model.dto.WorkspaceDto.ShowSectionsDto();
        showSections.setUrgentTasks(false);
        showSections.setQuickWins(false);
        showSections.setHabits(false);
        showSections.setReminders(false);
        workspace.setShowSections(showSections);

        // Empty workload assessment
        com.sonic.taskmanager.model.dto.WorkspaceDto.WorkloadAssessmentDto workloadAssessment = 
            new com.sonic.taskmanager.model.dto.WorkspaceDto.WorkloadAssessmentDto();
        workloadAssessment.setTotalTasks(0);
        workloadAssessment.setUrgentCount(0);
        workloadAssessment.setEstimatedHours(0.0);
        workloadAssessment.setRecommendation("No tasks found - time to add some goals!");
        workspace.setWorkloadAssessment(workloadAssessment);

        return workspace;
    }
}