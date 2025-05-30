package com.jobdam.jobdam_be.interview.exception;

import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
public enum InterviewErrorCode implements ErrorCode {
    NOT_FOUND_EXCEPTION(404,"인터뷰를 찾을 수 없습니다"),
    DB_INSERT_ERROR(500, "데이터베이스 입력오류가 발생했습니다.");
    private final int code;
    private final String message;
}
