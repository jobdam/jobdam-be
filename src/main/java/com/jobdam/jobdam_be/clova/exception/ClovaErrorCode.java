package com.jobdam.jobdam_be.clova.exception;

import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClovaErrorCode implements ErrorCode {
    AI_RESPONSE_INVALID(422, "AI 응답이 기대한 결과를 충족하지 못했습니다."),
    AI_RESPONSE_PARSING_FAILED(422, "AI 응답을 분할하는 데 실패했습니다. 예상한 형식과 다릅니다.");

    private final int code;
    private final String message;
}
