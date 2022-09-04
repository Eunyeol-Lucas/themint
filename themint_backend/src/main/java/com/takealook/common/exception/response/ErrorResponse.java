package com.takealook.common.exception.response;

import com.takealook.common.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse {
    private int status;
    private String message;
    private String code;

    public ErrorResponse(ErrorCode errorCode){
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
        this.code = errorCode.getErrorCode();
    }
}