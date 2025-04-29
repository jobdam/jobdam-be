package com.jobdam.jobdam_be.websokect.exception;

import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class WebSocketException extends RuntimeException{
    private final ErrorCode errorCode;

    public WebSocketException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
