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
import java.util.Optional;

//StompChannelInterCeptor에서 모든검증을 완료하고
//그다음에 리스너가 작동을한다.
//리스너에서는 오로지 트랙커 세션의 저장 및 삭제를 담당한다.
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

        tracker.addSession(roomId, accessor.getSessionId());

        log.info("[접속] purpose={} roomId={} sessionId={}" ,purpose,roomId,accessor.getSessionId());
    }


    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = event.getSessionId();

        BaseSessionInfo baseSessionInfo = Optional.ofNullable(accessor.getSessionAttributes())
                .map(attrs -> (BaseSessionInfo) attrs.get("baseSessionInfo"))
                .orElse(null);

        if (baseSessionInfo != null) {
            String purpose = baseSessionInfo.getPurpose();
            WebSocketSessionTracker tracker = trackerRegistry.getTracker(purpose);
            tracker.removeSession(baseSessionInfo.getRoomId(), sessionId);

            log.info("[웹소켓 정상 종료] purpose : {}, roomId : {}, sessionId : {}",
                    purpose, baseSessionInfo.getRoomId(), sessionId);
        } else{
            trackerRegistry.getAllTrackers().forEach(tracker -> {
                tracker.removeSession(sessionId);
            });
            log.warn("[웹소켓 비정상 종료] sessionId : {}", sessionId);
        }
    }

}