package com.jobdam.jobdam_be.websokect.event;

import com.jobdam.jobdam_be.auth.service.CustomUserDetails;
import com.jobdam.jobdam_be.websokect.exception.WebSocketException;
import com.jobdam.jobdam_be.websokect.exception.type.WebSocketErrorCode;
import com.jobdam.jobdam_be.websokect.sessionTracker.domain.model.BaseSessionInfo;
import com.jobdam.jobdam_be.websokect.sessionTracker.registry.SessionTrackerRegistry;
import com.jobdam.jobdam_be.websokect.sessionTracker.WebSocketSessionTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SessionTrackerRegistry trackerRegistry;

    @EventListener
    public void handleSessionConnect(SessionConnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        BaseSessionInfo baseSessionInfo = (BaseSessionInfo) Objects
                .requireNonNull(accessor.getSessionAttributes()).get("baseSessionInfo");

        String purpose = baseSessionInfo.getPurpose();
        String roomId = baseSessionInfo.getRoomId();
        WebSocketSessionTracker tracker = trackerRegistry.getTracker(purpose);

        //트랙커에 세션저장
        tracker.addSession(roomId, accessor.getSessionId());

        //아래유저아이디는 테스트용으로 확인하는거 실제는 지워야함.
        long userId = 0L;
        if (accessor.getUser() instanceof UsernamePasswordAuthenticationToken token) {
            CustomUserDetails userDetails = (CustomUserDetails) token.getPrincipal();
            userId = Long.parseLong(userDetails.getUsername());
        }
        log.info("[접속] purpose={} roomId={} userId={}", purpose,roomId,userId);
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event){
        String sessionId = event.getSessionId();

        //접속이 끊어지면 각 도메인(chat,signal)마다 지워줘야하는데
        //예외상황 발생시 key값을 못받아올수도 있음.(오로지 sessionId로만 제거필요)
        //그래서 전체 bean을 가져와서 지워줘야함
        trackerRegistry.getAllTrackers().forEach(tracker -> {
            tracker.removeSession(sessionId);
        });
        log.info("세션 연결 종료: {}", sessionId);
    }

}