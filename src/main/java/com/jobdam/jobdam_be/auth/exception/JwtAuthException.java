package com.jobdam.jobdam_be.auth.exception;

import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthException extends AuthenticationException {
    private final ErrorCode errorCode;

    public JwtAuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public JwtAuthException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause); // 부모 생성자로 cause 전달
        this.errorCode = errorCode;
    }
}
