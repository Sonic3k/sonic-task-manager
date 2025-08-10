package com.sonic.taskmanager.controller;

import com.sonic.taskmanager.model.dto.WorkspaceDto;
import com.sonic.taskmanager.model.response.BaseResponse;
import com.sonic.taskmanager.model.response.WorkspaceResponse;
import com.sonic.taskmanager.service.WorkspaceService;
import com.sonic.taskmanager.service.ReminderService;
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

    @GetMapping
    public WorkspaceResponse getTodaysWorkspace() {
        WorkspaceDto workspace = workspaceService.calculateTodaysWorkspace();
        
        WorkspaceResponse response = new WorkspaceResponse();
        response.setSuccess(true);
        response.setWorkspace(workspace);
        return response;
    }

    @PostMapping("/refresh")
    public WorkspaceResponse refreshWorkspace() {
        WorkspaceDto workspace = workspaceService.refreshWorkspace();
        
        WorkspaceResponse response = new WorkspaceResponse();
        response.setSuccess(true);
        response.setMessage("Workspace refreshed successfully");
        response.setWorkspace(workspace);
        return response;
    }

    @PutMapping("/reminders/{taskId}/snooze")
    public BaseResponse snoozeReminder(@PathVariable("taskId") Long taskId,
                                       @RequestParam(name = "days", defaultValue = "7") int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Days must be greater than 0");
        }
        
        reminderService.snoozeReminder(taskId, days);
        
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Reminder snoozed for " + days + " day(s)");
        return response;
    }

    @PutMapping("/reminders/{taskId}/acknowledge")
    public BaseResponse acknowledgeReminder(@PathVariable("taskId") Long taskId) {
        reminderService.acknowledgeReminder(taskId);
        
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        response.setMessage("Reminder acknowledged");
        return response;
    }
}