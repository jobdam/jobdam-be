package com.jobdam.jobdam_be.chat.exception;

import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum ChatErrorCode implements ErrorCode {
    INVALID_USER(404, "채팅방에 유저가 존재하지 않습니다");
    private final int code;
    private final String message;
}
