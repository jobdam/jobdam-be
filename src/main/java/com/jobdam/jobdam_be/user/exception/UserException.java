package com.jobdam.jobdam_be.user.exception;

import com.jobdam.jobdam_be.global.exception.AbstractException;
import com.jobdam.jobdam_be.global.exception.type.ErrorCode;


public class UserException extends AbstractException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause); // 부모 생성자로 cause 전달
    }
}
