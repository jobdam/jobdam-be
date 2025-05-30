package com.jobdam.jobdam_be.s3.exception;

import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements ErrorCode {
    INVALID_FILE(401, "유효하지 않은 파일입니다."),
    FILE_UPLOAD_FAILED(500, "파일 업로드에 실패하였습니다.");

    private final int code;
    private final String message;
}

