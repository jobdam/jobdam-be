package com.jobdam.jobdam_be.websokect.interceptor;

import com.jobdam.jobdam_be.auth.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String jwtToken = accessor.getFirstNativeHeader("Authorization");

        if(!StringUtils.hasText(jwtToken)) {
            log.warn("[웹소켓 연결 에러]JWT가 존재하지 않습니다. 연결을 종료합니다.");
            return null; // 인증 실패 시 세션끊기
        }
        if(!jwtToken.startsWith("Bearer ")){
            log.warn("[웹소켓 연결 에러]JWT 형식이 잘못되었습니다. 연결을 종료합니다.");
            return null;
        }
        jwtToken = jwtToken.substring(7);
        if(jwtProvider.isExpired(jwtToken)){//jwt 만료+검증
            log.warn("[웹소켓 연결 에러]JWT 검증에 실패하였습니다.");//만료이거나 검증이실패이거나 구분??
            return null;
        }

        Long userId = jwtProvider.getUserId(jwtToken);

        Objects.requireNonNull(accessor.getSessionAttributes()).put("userId", userId);

        return message;
    }

}
