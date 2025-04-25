package com.jobdam.jobdam_be.user.exception;

import com.jobdam.jobdam_be.global.exception.AbstractException;
import com.jobdam.jobdam_be.global.exception.type.ErrorCode;


public class UserException extends AbstractException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);

    }
}
