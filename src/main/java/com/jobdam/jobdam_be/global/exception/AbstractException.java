package com.jobdam.jobdam_be.global.exception;

import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public abstract class AbstractException extends RuntimeException {
    private final ErrorCode errorCode;

    public AbstractException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public AbstractException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause); // cause 전달!
        this.errorCode = errorCode;
    }

    public ErrorCode getExceptionResponse() {
        return errorCode;
    }

}
