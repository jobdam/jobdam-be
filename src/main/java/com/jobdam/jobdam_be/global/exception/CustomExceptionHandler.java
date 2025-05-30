package com.jobdam.jobdam_be.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    //커스텀한 예외처리 보내기.
    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<ErrorResponse> customExceptionHandle(AbstractException e){
        log.error("Exception occurred: {}",e.getMessage(), e);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(e.getExceptionResponse().getCode())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(e.getExceptionResponse().getCode()).body(errorResponse);
    }

    // 잘못된 요청
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message("요청하신 자원을 찾을 수 없습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    //nullPoint 예외처리
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> nullPointExceptionHandle(NullPointerException e){
        log.error("nullPointException occurred: {}",e.getMessage(), e);
        int errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode)
                .message("서버에서 알 수 없는 에러가 발생했습니다. 다시 시도해 주세요")
                .build();

        return ResponseEntity.status(errorCode).body(errorResponse);
    }

    //나머지 예외처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> ExceptionHandle(Exception e) {
        log.error("Exception occurred: {}",e.getMessage(), e);
        int errorCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode)
                .message("서버에서 알 수 없는 에러가 발생했습니다. 다시 시도해 주세요")
                .build();
        return ResponseEntity.status(errorCode).body(errorResponse);
    }
}