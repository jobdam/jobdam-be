package com.jobdam.jobdam_be.user.exception;

import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_INFO_NOT_FOUND(404, "유저가 정보를 가지고 있지 않습니다."),
    PROFILE_UPDATE_FAILED(401, "프로필 업데이트에 실패하였습니다."),
    DUPLICATE_USER_ID(1, "이미 가입된 사용자입니다.");

    private final int code;
    private final String message;
}
