package com.takealook.common.exception.member;

import com.takealook.common.exception.code.ErrorCode;
import lombok.Getter;

@Getter
public class NicknameDuplicateException extends RuntimeException{
    private ErrorCode errorCode;

    public NicknameDuplicateException(String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
