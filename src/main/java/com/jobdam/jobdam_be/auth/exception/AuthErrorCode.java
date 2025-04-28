package com.jobdam.jobdam_be.auth.exception;

import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum AuthErrorCode implements ErrorCode {
    DUPLICATE_EMAIL(400, "이미 가입된 이메일입니다."),
    EMPTY_EMAIL_OR_PASSWORD(400, "이메일 또는 비밀번호가 누락되었습니다."),
    INVALID_EMAIL_OR_PASSWORD(400, "이메일 또는 비밀번호가 틀렸습니다."),

    INVALID_REQUEST(401, "로그인에 실패했습니다."),
    UNAUTHORIZED_REQUEST(401, "인증되지 않은 요청입니다."),
    EXPIRED_TOKEN(401, "토큰이 만료되었습니다."),
    INVALID_TOKEN(401, "잘못된 토큰입니다."),
    INVALID_SIGNATURE(401, "JWT 서명이 일치하지 않습니다."),

    EMAIL_VERIFICATION_REQUIRED(403, "이메일 인증을 먼저 진행하세요."),

    TOKEN_NOT_FOUND(404, "토큰이 존재하지 않습니다."),
    INVALID_USER(404, "존재하지 않는 사용자입니다."),

    DB_ERROR(500, "데이터베이스 오류가 발생했습니다."),
    MAIL_SEND_ERROR(500, "메일 전송에 실패하였습니다."),
    UNSUPPORTED_TYPE(500, "잘못된 형식의 입력입니다."),
    UNKNOWN_ERROR(500, "식별되지 않은 오류입니다.");

    private final int code;
    private final String message;
}
