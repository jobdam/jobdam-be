package com.jobdam.jobdam_be.chat.exception;

import com.jobdam.jobdam_be.global.exception.AbstractException;
import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class ChatException extends AbstractException {
    public ChatException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChatException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause); // 부모 생성자로 cause 전달
    }
}
