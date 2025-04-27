package com.jobdam.jobdam_be.websokect.exception;

import com.jobdam.jobdam_be.global.exception.ErrorResponse;
import com.jobdam.jobdam_be.websokect.exception.type.WebSocketErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class WebSocketExceptionHandler {
    private final SimpMessagingTemplate messagingTemplate;

    @MessageExceptionHandler(WebSocketException.class)
    public void handleWebSocketException(WebSocketException e, Principal principal) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(e.getErrorCode().getCode())
                .message(e.getMessage())
                .build();

        messagingTemplate.convertAndSendToUser(//1:1로 에러보내는 방법!
                principal.getName(), // 접속한 사용자
                "/queue/error",      // 클라이언트가 구독할 경로
                errorResponse
        );

        log.warn("[웹소켓 에러 발생] 에러코드 : {} 에러메세지 : {}", e.getErrorCode().getCode(), e.getMessage());
    }
}
