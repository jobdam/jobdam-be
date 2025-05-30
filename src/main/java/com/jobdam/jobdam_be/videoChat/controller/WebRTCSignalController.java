package com.jobdam.jobdam_be.videoChat.controller;

import com.jobdam.jobdam_be.videoChat.dto.CandidateSignalDTO;
import com.jobdam.jobdam_be.videoChat.dto.JoinListSignalDTO;
import com.jobdam.jobdam_be.videoChat.dto.SdpSignalDTO;
import com.jobdam.jobdam_be.websokect.sessionTracker.domain.WebRTCSignalSessionTracker;
import com.jobdam.jobdam_be.videoChat.type.SignalType;
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

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebRTCSignalController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final WebRTCSignalSessionTracker tracker;

    private String destination(String roomId){
        return "/queue/signal/"+roomId;
    }

    //처음입장시 리스트들을 받아서 보내줌
    @MessageMapping("/signal/join/{roomId}")
    public void joinHandle(@DestinationVariable String roomId,
                           Principal principal,
                           @Header("simpSessionId") String sessionId) {
        Long enterUserId = Long.valueOf(principal.getName());

       //세션 유저 맵핑(principal 말고 다른유저를 list로 보여주기위해 필요)
        tracker.addSessionUserMapping(sessionId,enterUserId);

        //기존 참가자 목록을 조회하기
        List<Long> allUserIdList = tracker.getAllUserIds(roomId);

        //방인원 전체에게 리스트 목록전체 보내줌(동기화방지로 프론트에서 체크함)
        if(!allUserIdList.isEmpty()) {
            simpMessagingTemplate.convertAndSend(
                    "/topic/signal/" + roomId,
                    new JoinListSignalDTO(SignalType.JOIN_LIST,allUserIdList)
            );
        }

        log.info("[SIGNAL서버 JOIN_List요청] roomId:{} userId:{} sessionId:{}",roomId,enterUserId,sessionId);
    }
    //새로고침해서 한명만 다시 연결시도하면!
    @MessageMapping("/signal/joinOne/{roomId}")  //
    public void joinOneHandle(@DestinationVariable String roomId,
                           Principal principal,
                           @Header("simpSessionId") String sessionId) {
        Long enterUserId = Long.valueOf(principal.getName());

        //세션 유저 맵핑(principal 말고 다른유저를 list로 보여주기위해 필요)
        tracker.addSessionUserMapping(sessionId,enterUserId);

        //기존 참가자 목록을 조회하기
        List<Long> existingUserIdList = tracker.getOtherUserIds(roomId,sessionId);

        //나에게 참여중인 유저목록 보내주기
        if(!existingUserIdList.isEmpty()) {
            simpMessagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    destination(roomId),
                    new JoinListSignalDTO(SignalType.JOIN_ONE,existingUserIdList)
            );
        }

        log.info("[SIGNAL서버 JOIN_ONE요청] roomId:{} userId:{} sessionId:{}",roomId,enterUserId,sessionId);
    }

    //내가 상대방에게 p2p연결 하고싶다는 신호(상대방이 나의정보를 얻음)
    @MessageMapping("/signal/offer/{roomId}")
    public void offer(@DestinationVariable String roomId,
                      @Payload SdpSignalDTO.Request requestDTO,
                      Principal principal) {

        Long senderId = Long.valueOf(principal.getName());
        SdpSignalDTO.Response responseDTO = SdpSignalDTO.Response.builder()
                .signalType(SignalType.OFFER)
                .senderId(senderId)
                .receiverId(requestDTO.getReceiverId())
                .sdp(requestDTO.getSdp())
                .build();

        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(requestDTO.getReceiverId()),
                destination(roomId),
                responseDTO
        );
        log.info("[SIGNAL서버 OFFER요청] sendId:{} receiverId:{}",
                senderId,requestDTO.getReceiverId());
    }

    // 상대방이 p2p offer 대한 응답을줌 (내가 상대방의 정보를 얻음)
    @MessageMapping("/signal/answer/{roomId}")
    public void answer(@DestinationVariable String roomId,
                       @Payload SdpSignalDTO.Request requestDTO,
                       Principal principal) {

        Long senderId = Long.valueOf(principal.getName());
        SdpSignalDTO.Response responseDTO = SdpSignalDTO.Response.builder()
                .signalType(SignalType.ANSWER)
                .senderId(senderId)
                .receiverId(requestDTO.getReceiverId())
                .sdp(requestDTO.getSdp())
                .build();

        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(requestDTO.getReceiverId()),
                destination(roomId),
                responseDTO
        );

        log.info("[SIGNAL서버 ANSWER요청] sendId:{} receiverId:{}",
                senderId,requestDTO.getReceiverId());
    }

    // 네트워크 우회를 통한 후보경로들을 교환함 연결이 안될 경우를 대비
    @MessageMapping("/signal/candidate/{roomId}")
    public void candidate(@DestinationVariable String roomId,
                          @Payload CandidateSignalDTO.Request requestDTO,
                          Principal principal) {

        Long senderId = Long.valueOf(principal.getName());
        CandidateSignalDTO.Response responseDTO = CandidateSignalDTO.Response.builder()
                .signalType(SignalType.CANDIDATE)
                .senderId(senderId)
                .receiverId(requestDTO.getReceiverId())
                .candidate(requestDTO.getCandidate())
                .sdpMid(requestDTO.getSdpMid())
                .sdpMLineIndex(requestDTO.getSdpMLineIndex())
                .build();

        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(requestDTO.getReceiverId()),
                destination(roomId),
                responseDTO
        );

        log.info("[SIGNAL서버 CANDIDATE요청] sendId:{} receiverId:{}",
                senderId,requestDTO.getReceiverId());
    }
}
