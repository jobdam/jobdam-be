package com.jobdam.jobdam_be.clova.exception;

import com.jobdam.jobdam_be.global.exception.AbstractException;
import com.jobdam.jobdam_be.global.exception.type.ErrorCode;

public class ClovaException extends AbstractException {

    public ClovaException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ClovaException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause); // 부모 생성자로 cause 전달
    }
}