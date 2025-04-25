package com.jobdam.jobdam_be.global.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode{
    USER_NOT_FOUND(404, "해당하는 정보의 사용자를 찾을 수 없습니다."),
    INVALID_AUTH_TOKEN(401,"유효하지 않는 토큰입니다");
//    DUPLICATE_RESOURCE(HttpStatus.CONFLICT,"데이터가 이미 존재 합니다"),
//    ACCESS_DENIED(HttpStatus.FORBIDDEN,"접근권한이 없습니다"),
//    INVALID_REQUEST(HttpStatus.BAD_REQUEST,"요청 형식이 올바르지 않습니다"),
//    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버 오류가 발생했습니다");
    private final int code;
    private final String message;


}
