package com.jobdam.jobdam_be.auth.exception;

import com.jobdam.jobdam_be.global.exception.AbstractException;
import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class AuthException extends AbstractException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode);

    }
}
