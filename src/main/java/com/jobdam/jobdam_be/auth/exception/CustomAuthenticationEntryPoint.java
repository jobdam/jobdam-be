package com.jobdam.jobdam_be.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import com.jobdam.jobdam_be.global.exception.ErrorResponse;

import static com.jobdam.jobdam_be.auth.exception.AuthErrorCode.*;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        AuthErrorCode errorCode = UNAUTHORIZED_REQUEST;

        // 기본적으로 UNAUTHORIZED 에러코드 사용
        if(request.getAttribute("exception") != null && request.getAttribute("exception") instanceof AuthErrorCode e)
            errorCode = e;

        sendErrorResponse(response, errorCode);
    }

    private void sendErrorResponse(HttpServletResponse response, AuthErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getCode());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage()).build();


        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
