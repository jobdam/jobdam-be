package com.jobdam.jobdam_be.websokect.event;

import com.jobdam.jobdam_be.websokect.sessionTracker.SessionTrackerRegistry;
import com.jobdam.jobdam_be.websokect.sessionTracker.WebSocketSessionTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SessionTrackerRegistry trackerRegistry;

    @EventListener
    public void handleSessionConnect(SessionConnectedEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String purpose = accessor.getFirstNativeHeader("purpose");
        String sessionId = accessor.getSessionId();

        //registry에 저장된 구분값(chat,signal 등등)으로 tracker 가져옴
        WebSocketSessionTracker tracker = trackerRegistry.getTracker(purpose);

        if (tracker != null) {
            String key = accessor.getFirstNativeHeader(tracker.getKeyHeader());
            tracker.addSession(key, sessionId);
            System.out.println("[접속] purpose=" + purpose + ", key=" + key + ", session=" + sessionId);
        } else {
            log.error("웹소켓 연결 중 에러 발생");
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event){
        String sessionId = event.getSessionId();

        //접속이 끊어지면 각 도메인(chat,signal)마다 지워줘야하는데
        //예외상황 발생시 key값을 못받아올수도 있음.
        //그래서 전체 bean을 가져와서 지워줘야함
        trackerRegistry.getAllTrackers().forEach(tracker -> {
            tracker.removeSession(sessionId);
        });

        log.info("세션 연결 종료: {}", sessionId);
    }
}
