package com.jobdam.jobdam_be.websokect.event;

import com.jobdam.jobdam_be.websokect.sessionTracker.registry.SessionTrackerRegistry;
import com.jobdam.jobdam_be.websokect.sessionTracker.WebSocketSessionTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

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

        //만약 tracker를 사용안하면 purpose가 있냐없냐에 따라서 아래조건하거나말거나 해야함.
        //현재는 있다는 전제 하에 코드진행 중.

        // purpose를 세션 속성에 저장 (삭제시 빠르게하기위해)
        Objects.requireNonNull(accessor.getSessionAttributes(),
                "웹소켓 연결 중 purpose null 발생!").put("purpose", purpose);

        //registry에 저장된 구분값(chat,signal 등등)으로 tracker 가져옴
        //그안에서 tracker에 저장한 key이름으로 roomId값을 가져옴(ex videoChatRoomId : 3)
        WebSocketSessionTracker tracker = trackerRegistry.getTracker(purpose);
        String roomId = accessor.getFirstNativeHeader(tracker.getKeyHeader());

        //세션속성에 key값 저장
        accessor.getSessionAttributes().put("roomId",roomId);
        //jwt로 아이디 추출해야함

        tracker.addSession(roomId, sessionId);
        log.info("[접속] purpose={} roomId={} sessionId={}", purpose, roomId, sessionId);
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