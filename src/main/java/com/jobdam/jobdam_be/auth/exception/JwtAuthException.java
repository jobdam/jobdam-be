package com.jobdam.jobdam_be.auth.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthException extends AuthenticationException {
    private final AuthErrorCode errorCode;

    public JwtAuthException(AuthErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
