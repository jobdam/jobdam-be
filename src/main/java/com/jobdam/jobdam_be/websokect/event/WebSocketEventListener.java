package com.jobdam.jobdam_be.websokect.event;

import com.jobdam.jobdam_be.websokect.exception.WebSocketException;
import com.jobdam.jobdam_be.websokect.exception.type.WebSocketErrorCode;
import com.jobdam.jobdam_be.websokect.model.WebSocketBaseSessionInfo;
import com.jobdam.jobdam_be.websokect.sessionTracker.SessionTrackerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Objects;
import java.util.Optional;

//StompChannelInterCeptor에서 유저검증을 완료하고
//그다음에 리스너가 작동을한다.
//리스너에서는 purpose,roomId를 검증하고
//트랙커 세션의 저장 및 삭제를 담당한다.
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SessionTrackerRegistry trackerRegistry;

    @EventListener
    public void handleSessionConnect(SessionConnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        log.info("[웹소켓 연결!] sessionId={}", accessor.getSessionId());
    }

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println(accessor.getDestination());
        WebSocketBaseSessionInfo webSocketBaseSessionInfo =
                buildSessionInfoFromDestination(accessor.getDestination());

        if(Objects.isNull(webSocketBaseSessionInfo)){
            log.info("[웹소켓 구독 error] sessionId={}", accessor.getSessionId());
            return;
        }
        Objects.requireNonNull(accessor.getSessionAttributes())
                .put("webSocketBaseSessionInfo", webSocketBaseSessionInfo);

        String purpose = webSocketBaseSessionInfo.getPurpose();
        String roomId = webSocketBaseSessionInfo.getRoomId();

        trackerRegistry.getTracker(purpose)
               .addSession(roomId, accessor.getSessionId());

        log.info("[웹소켓 구독 완료!] purpose={} roomId={} sessionId={}" ,purpose,roomId,accessor.getSessionId());
    }

    @EventListener
    public void handleSessionUnsubscribe(SessionUnsubscribeEvent event) {//구독 취소!
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        removeSession(accessor,sessionId,"[웹소켓 구독취소]");
    }

    //강제 종료시!! 현재 하나만지움
    //추가 구독 발생시 구조변경해줘야함
    //ex)알림,에러 구독시 변경필요
    //어떻게 될지몰라 일단그대로쓴다..
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = event.getSessionId(); //accessor을 못받아 올 수 있으므로 event에서 세션아이디 가져옴
        removeSession(accessor,sessionId,"[웹소켓 DISCONNECT]");
    }
    //이벤트리스너 끝 ---------------

   //아래메소드 들은 유틸메소드
    private WebSocketBaseSessionInfo buildSessionInfoFromDestination(String destination){
        if(Objects.isNull(destination) || destination.isBlank()) {
            throw new WebSocketException(WebSocketErrorCode.MISSING_SUBSCRIBE);
        }
        String[] parts = destination.split("/",6);//악의적인 요청시 제한5
        String brokerPrefix = parts[1];//topic or user (브로드캐스트 or 1:1)

        String purpose;
        String roomId;
        if(! ("topic".equals(brokerPrefix) || "user".equals(brokerPrefix) )) {
            throw new WebSocketException(WebSocketErrorCode.INVALID_BROKER_PREFIX);//잘못 온 경우
        }

        if("topic".equals(brokerPrefix)) {
            purpose = parts[2]; //matching, chat, signal
            roomId = parts[3];
        }else {
            purpose = parts[3];
            roomId = parts[4];
            if("error".equals(purpose))
                return null;
        }
        validatePurposeAndRoomId(purpose,roomId);

        return WebSocketBaseSessionInfo.builder()//세션구분정보
                .purpose(purpose)
                .roomId(roomId)
                .build();
    }

    private void validatePurposeAndRoomId(String purpose, String roomId){
        if(Objects.isNull(purpose) || purpose.isBlank()){
            throw new WebSocketException(WebSocketErrorCode.MISSING_PURPOSE);
        }
        if(!trackerRegistry.checkKey(purpose)){
            throw new WebSocketException(WebSocketErrorCode.INVALID_PURPOSE);
        }
        if(Objects.isNull(roomId) || roomId.isBlank()){
            throw new WebSocketException(WebSocketErrorCode.MISSING_ROOM_ID);
        }
    }

    // 제거메서드
    private void removeSession(StompHeaderAccessor accessor, String sessionId, String Type) {

        WebSocketBaseSessionInfo webSocketBaseSessionInfo = Optional.ofNullable(accessor.getSessionAttributes())
                .map(attrs -> (WebSocketBaseSessionInfo) attrs.get("webSocketBaseSessionInfo"))
                .orElse(null);

        if (Objects.isNull(webSocketBaseSessionInfo)) {
            trackerRegistry.getAllTrackers().forEach(tracker -> {
                tracker.removeSession(sessionId);
            });
            log.warn("{}(비정상루트)] sessionId = {}",Type, sessionId);
        } else{
            trackerRegistry.getTracker(webSocketBaseSessionInfo.getPurpose())
                    .removeSession(webSocketBaseSessionInfo.getRoomId(), sessionId);

            log.info("{} purpose = {}, roomId = {}, sessionId = {}", Type,
                    webSocketBaseSessionInfo.getPurpose(), webSocketBaseSessionInfo.getRoomId(), sessionId);
        }
    }
}