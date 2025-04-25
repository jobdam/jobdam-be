package com.jobdam.jobdam_be.user.exception;

import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
    DUPLICATE_USER_ID(1, "이미 가입된 사용자입니다.");

    private final int code;
    private final String message;
}
