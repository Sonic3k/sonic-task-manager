package com.sonic.taskmanager.model.response;

import com.sonic.taskmanager.model.dto.WorkspaceDto;

public class WorkspaceResponse extends BaseResponse {
    private WorkspaceDto workspace;

    public WorkspaceResponse() {
        super();
    }

    public WorkspaceDto getWorkspace() { return workspace; }
    public void setWorkspace(WorkspaceDto workspace) { this.workspace = workspace; }
}
