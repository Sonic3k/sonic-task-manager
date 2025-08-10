package com.sonic.taskmanager.controller;

import com.sonic.taskmanager.model.dto.WorkspaceDto;
import com.sonic.taskmanager.model.response.BaseResponse;
import com.sonic.taskmanager.model.response.WorkspaceResponse;
import com.sonic.taskmanager.service.WorkspaceService;
import com.sonic.taskmanager.service.ReminderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/workspace")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final ReminderService reminderService;

    public WorkspaceController(WorkspaceService workspaceService, 
                              ReminderService reminderService) {
        this.workspaceService = workspaceService;
        this.reminderService = reminderService;
    }

    /**
     * Get today's complete workspace data
     * This is the main endpoint that frontend calls
     */
    @GetMapping
    public WorkspaceResponse getTodaysWorkspace() {
        try {
            WorkspaceDto workspace = workspaceService.calculateTodaysWorkspace();
            
            WorkspaceResponse response = new WorkspaceResponse();
            response.setSuccess(true);
            response.setWorkspace(workspace);
            return response;
            
        } catch (Exception e) {
            // Log error but don't expose internal details
            System.err.println("Error calculating workspace: " + e.getMessage());
            e.printStackTrace();
            
            // Return empty workspace instead of error
            WorkspaceDto emptyWorkspace = createEmptyWorkspace();
            WorkspaceResponse response = new WorkspaceResponse();
            response.setSuccess(true);
            response.setMessage("Workspace loaded with default data");
            response.setWorkspace(emptyWorkspace);
            return response;
        }
    }

    /**
     * Refresh/recalculate workspace data
     * Call this after task updates to get fresh data
     */
    @PostMapping("/refresh")
    public WorkspaceResponse refreshWorkspace() {
        try {
            WorkspaceDto workspace = workspaceService.refreshWorkspace();
            
            WorkspaceResponse response = new WorkspaceResponse();
            response.setSuccess(true);
            response.setMessage("Workspace refreshed successfully");
            response.setWorkspace(workspace);
            return response;
            
        } catch (Exception e) {
            System.err.println("Error refreshing workspace: " + e.getMessage());
            e.printStackTrace();
            
            WorkspaceDto emptyWorkspace = createEmptyWorkspace();
            WorkspaceResponse response = new WorkspaceResponse();
            response.setSuccess(true);
            response.setMessage("Workspace refreshed with default data");
            response.setWorkspace(emptyWorkspace);
            return response;
        }
    }

    /**
     * Snooze a reminder
     */
    @PutMapping("/reminders/{taskId}/snooze")
    public ResponseEntity<BaseResponse> snoozeReminder(@PathVariable("taskId") Long taskId,
                                                       @RequestParam(defaultValue = "7") int days) {
        try {
            reminderService.snoozeReminder(taskId, days);
            
            BaseResponse response = new BaseResponse();
            response.setSuccess(true);
            response.setMessage("Reminder snoozed for " + days + " day(s)");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setSuccess(false);
            response.setError("Failed to snooze reminder");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Acknowledge a reminder (mark as seen)
     */
    @PutMapping("/reminders/{taskId}/acknowledge")
    public ResponseEntity<BaseResponse> acknowledgeReminder(@PathVariable("taskId") Long taskId) {
        try {
            reminderService.acknowledgeReminder(taskId);
            
            BaseResponse response = new BaseResponse();
            response.setSuccess(true);
            response.setMessage("Reminder acknowledged");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            BaseResponse response = new BaseResponse();
            response.setSuccess(false);
            response.setError("Failed to acknowledge reminder");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Create empty workspace for error cases
     */
    private WorkspaceDto createEmptyWorkspace() {
        WorkspaceDto workspace = new WorkspaceDto();
        workspace.setFocusTask(null);
        workspace.setNextUpStack(java.util.Collections.emptyList());
        workspace.setQuickWins(java.util.Collections.emptyList());
        workspace.setActiveReminders(java.util.Collections.emptyList());
        workspace.setDailyMood("relaxed");

        // Empty show sections
        WorkspaceDto.ShowSectionsDto showSections = new WorkspaceDto.ShowSectionsDto();
        showSections.setUrgentTasks(false);
        showSections.setQuickWins(false);
        showSections.setHabits(false);
        showSections.setReminders(false);
        workspace.setShowSections(showSections);

        // Empty workload assessment
        WorkspaceDto.WorkloadAssessmentDto workloadAssessment = new WorkspaceDto.WorkloadAssessmentDto();
        workloadAssessment.setTotalTasks(0);
        workloadAssessment.setUrgentCount(0);
        workloadAssessment.setEstimatedHours(0.0);
        workloadAssessment.setRecommendation("No tasks found - time to add some goals!");
        workspace.setWorkloadAssessment(workloadAssessment);

        return workspace;
    }
}