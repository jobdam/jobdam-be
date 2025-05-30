package com.jobdam.jobdam_be.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobdam.jobdam_be.global.exception.type.ErrorCode;
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
        ErrorCode errorCode = UNAUTHORIZED_REQUEST;
        if (authException instanceof JwtAuthException ex) {
            errorCode = ex.getErrorCode();  // errorCode 꺼내기
        }
        else if (request.getAttribute("exception") != null && request.getAttribute("exception") instanceof AuthErrorCode e) {
            log.info(e.getMessage(), e);
            errorCode = e;
        }

        sendErrorResponse(response, errorCode);
    }

    private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
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
