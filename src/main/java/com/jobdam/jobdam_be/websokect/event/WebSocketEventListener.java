package com.jobdam.jobdam_be.websokect.event;

import com.jobdam.jobdam_be.websokect.sessionTracker.domain.model.BaseSessionInfo;
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
        String purpose = accessor.getFirstNativeHeader("purpose");//match,chat,signal구분

        if(!validatePurpose(purpose)) {
            return;
        }
        //purpose로 일치하는 tracker 가져옴
        WebSocketSessionTracker tracker = trackerRegistry.getTracker(purpose);
        //단계 2 roomID가져오기(클라가 chatroomId : 135ab나 matchroomId : raa3 이런식으로 보내는데)
        //tracker에서 일치하는 roomkey(chatroomId)를 가져와서 클라헤더에서 id(135ab)를 꺼냄
        String roomId = accessor.getFirstNativeHeader(tracker.getRoomKeyHeader());
        if(!validateRoomId(roomId)) {
            return;
        }

        // 세션 attributes에서 유저 ID 가져오기(검증단계에서 넣음)
        Long userId = (Long) Objects.requireNonNull(accessor.getSessionAttributes()).get("userId");
        accessor.getSessionAttributes().remove("userId");//기존 세션속성제거

        //세션 속성에 기본정보 객체 넣기
        accessor.getSessionAttributes().put("baseSessionInfo", BaseSessionInfo.builder()
                .purpose(purpose)
                .roomId(roomId)
                .userId(userId)
                .build());

        //트랙커에 세션저장
        tracker.addSession(roomId, accessor.getSessionId());
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

    private boolean validatePurpose(String purpose){
        if(Objects.isNull(purpose)){
            log.warn("[웹소켓 연결 에러] purpose가 없습니다(null!)");
            return false;
        }
        if(!trackerRegistry.checkKey(purpose)){
            log.warn("[웹소켓 연결 에러] 일치하는 purpose가 없습니다. purpose={}",purpose);
            return false;
        }
        return true;
    }

    private boolean validateRoomId(String roomId){
        if(Objects.isNull(roomId)){
            log.warn("[웹소켓 연결 에러] roomId가 없습니다(null!)");
            return false;
        }
        return true;
    }
}