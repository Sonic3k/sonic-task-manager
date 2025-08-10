package com.sonic.taskmanager.model.response;

import com.sonic.taskmanager.model.dto.BulkOperationResultDto;

public class BulkOperationResponse extends BaseResponse {
    private BulkOperationResultDto result;

    public BulkOperationResponse() {
        super();
    }

    public BulkOperationResultDto getResult() {
        return result;
    }

    public void setResult(BulkOperationResultDto result) {
        this.result = result;
    }
}