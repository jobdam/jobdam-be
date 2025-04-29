package com.jobdam.jobdam_be.websokect.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class TestController {
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @MessageMapping("/chat.send")  // 클라이언트가 /app/chat.send로 보내면 여기로 매핑
    public void sendMessage(@Payload String s, Principal principal, StompHeaderAccessor headerAccessor) {

        log.info("StompHeaderAccessor User: {}", headerAccessor.getUser());
        if (principal == null) {
            log.warn("Principal 정보 없음. 인증 안 된 사용자 요청!");
            return;
        }
        String username = principal.getName(); // 여기서 userId 또는 username을 꺼낼 수 있음
        System.out.println("userId"+username);
        log.info("받은 메시지 - 보낸 사람: {}내용: {}",
                username, s
        );
    }
}
