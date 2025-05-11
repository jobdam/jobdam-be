package com.jobdam.jobdam_be.s3.exception;

import com.jobdam.jobdam_be.global.exception.AbstractException;
import com.jobdam.jobdam_be.global.exception.type.ErrorCode;

public class S3Exception extends AbstractException {

    public S3Exception(ErrorCode errorCode) {
        super(errorCode);
    }

    public S3Exception(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
