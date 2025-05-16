package com.jobdam.jobdam_be.interview.exception;

import com.jobdam.jobdam_be.global.exception.AbstractException;
import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class InterviewException extends AbstractException {
    public InterviewException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InterviewException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
