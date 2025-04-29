package com.jobdam.jobdam_be.websokect.exception.type;

import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WebSocketErrorCode implements ErrorCode {
    TOKEN_MISSING(4001, "[웹소켓 연결 에러]JWT가 존재하지 않습니다."),
    JWT_INVALID(4002, "[웹소켓 연결 에러]JWT 검증 실패"),
    JWT_UNKNOWN_ERROR(4003, "[웹소켓 연결 에러]JWT 검증 단계에서 알 수 없는 에러 발생"),
    JWT_EXPIRED(4004, "[웹소켓 연결 에러]JWT 만료됨"),
    MISSING_PURPOSE(400, "[웹소켓 연결 에러]목적(purpose) 헤더가 없습니다."),
    INVALID_PURPOSE(400, "[웹소켓 연결 에러]유효하지 않은 purpose입니다."),
    MISSING_ROOM_ID(400, "[웹소켓 연결 에러]roomId 헤더가 없습니다.");



    private final int code;
    private final String message;
}
