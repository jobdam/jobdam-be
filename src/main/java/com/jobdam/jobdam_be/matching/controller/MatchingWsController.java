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

    public void matchingComplete( List<MatchWaitingUserInfo> fullList, String roomId) {
        fullList.forEach(participant ->
            simpMessagingTemplate.convertAndSendToUser(
                    String.valueOf(participant.getUserId()),
                    "/queue/match/"+participant.getJobGroup(),
                    roomId
            ));
    }
}
