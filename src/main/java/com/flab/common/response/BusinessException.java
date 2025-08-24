package com.flab.common.response;

import com.flab.common.exception.ErrorCode;
import lombok.Getter;


@Getter
public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;
    private final String errorMessage;


    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage != null ? customMessage : errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorMessage = customMessage != null ? customMessage : errorCode.getMessage();
    }
}
