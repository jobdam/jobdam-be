package com.jobdam.jobdam_be.websokect.controller;

import com.jobdam.jobdam_be.websokect.dto.webRTCSignal.CandidateSignalDTO;
import com.jobdam.jobdam_be.websokect.dto.webRTCSignal.SdpSignalDTO;
import com.jobdam.jobdam_be.websokect.sessionTracker.domain.WebRTCSignalSessionTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebRTCSignalController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WebRTCSignalSessionTracker tracker;

    @MessageMapping("/signal/join/{roomId}")  // 클라이언트가 /app/chat.send로 보내면 여기로 매핑
    public void joinHandle(@DestinationVariable String roomId,
                           Principal principal,
                           @Header("simpSessionId") String sessionId) {
        Long enterUserId = Long.valueOf(principal.getName());

       //세션 유저 맵핑(principal 말고 다른유저를 list로 보여주기위해 필요)
        tracker.addSessionUserMapping(sessionId,enterUserId);

        //기존 참가자 목록을 조회하기
        List<Long> existingUserIdList = tracker.getOtherUserIds(roomId,sessionId);

        //나에게 참여중인 유저목록 보내주기
        simpMessagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/signal/"+roomId,
                Map.of("type", "JOIN_LIST", "userIdList", existingUserIdList)
        );

        //나를 제외한 유저들에게 내정보 보내주기
        existingUserIdList.forEach(
                userId -> simpMessagingTemplate.convertAndSendToUser(
                        tracker.getSessionId(userId),
                        "/queue/signal/"+roomId,
                        Map.of("type","JOIN_ONE", "userId",enterUserId)
                )
        );

        log.info("[SIGNAL서버 JOIN요청] roomId:{} userId:{} sessionId:{}",roomId,enterUserId,sessionId);
    }

    //내가 상대방에게 p2p연결 하고싶다는 신호(상대방이 나의정보를 얻음)
    @MessageMapping("/signal/offer/{roomId}")
    public void offer(@DestinationVariable String roomId,
                      @Payload SdpSignalDTO dto) {
        String targetSession = tracker.getSessionId(dto.getReceiverId());
        simpMessagingTemplate.convertAndSendToUser(
                targetSession,
                "/queue/signal/" + roomId,
                Map.of(
                        "type", "OFFER",
                        "senderId", dto.getSenderId(),
                        "sdp", dto.getSdp()
                )
        );
    }

    // 상대방이 p2p offer 대한 응답을줌 (내가 상대방의 정보를 얻음)
    @MessageMapping("/signal/answer/{roomId}")
    public void answer(@DestinationVariable String roomId,
                       @Payload SdpSignalDTO dto) {
        String targetSession = tracker.getSessionId(dto.getReceiverId());
        simpMessagingTemplate.convertAndSendToUser(
                targetSession,
                "/queue/signal/" + roomId,
                Map.of(
                        "type", "ANSWER",
                        "senderId", dto.getSenderId(),
                        "sdp", dto.getSdp()
                )
        );
    }

    // 네트워크 우회를 통한 후보경로들을 교환함 연결이 안될 경우를 대비
    @MessageMapping("/signal/candidate/{roomId}")
    public void candidate(@DestinationVariable String roomId,
                          @Payload CandidateSignalDTO dto) {
        String targetSession = tracker.getSessionId(dto.getReceiverId());
        simpMessagingTemplate.convertAndSendToUser(
                targetSession,
                "/queue/signal/" + roomId,
                Map.of(
                        "type", "CANDIDATE",
                        "senderId", dto.getSenderId(),
                        "candidate", dto.getCandidate(),
                        "sdpMid", dto.getSdpMid(),
                        "sdpMLineIndex", dto.getSdpMLineIndex()
                )
        );
    }
}
