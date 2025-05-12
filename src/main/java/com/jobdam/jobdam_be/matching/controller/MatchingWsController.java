package com.jobdam.jobdam_be.matching.controller;

import com.jobdam.jobdam_be.matching.model.MatchWaitingUserInfo;
import com.jobdam.jobdam_be.videoChat.dto.JoinListSignalDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MatchingWsController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    //매칭 완료되면 그사람에게 참여 신호를줌
    public void matchingComplete( List<MatchWaitingUserInfo> fullList, String roomId) {
        fullList.forEach(participant ->
            joinChatRoom(participant.getUserId(), participant.getJobGroup(), roomId));
    }
    //매칭중인유저가 3~6명방일때 비어있는방 참여
     public void userEnterEmptyRoom(MatchWaitingUserInfo userInfo, String roomId) {
        joinChatRoom(userInfo.getUserId(), userInfo.getJobGroup(), roomId);
    }

    private void joinChatRoom(Long userId, String jopGroup, String roomId){
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/match/" + jopGroup,
                roomId
        );
    }
}
