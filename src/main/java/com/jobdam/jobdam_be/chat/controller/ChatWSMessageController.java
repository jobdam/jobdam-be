package com.jobdam.jobdam_be.chat.controller;

import com.jobdam.jobdam_be.auth.service.CustomUserDetails;
import com.jobdam.jobdam_be.chat.dto.ChatMessageDto;
import com.jobdam.jobdam_be.chat.type.ChatUserStatusType;
import com.jobdam.jobdam_be.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWSMessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserService userService;
    //채팅 보내기
    @MessageMapping("/chat/send/{roomId}")
    public void sendChat(@DestinationVariable String roomId,
                         ChatMessageDto.Request request,
                         Principal principal) {

        CustomUserDetails user = (CustomUserDetails) ((Authentication) principal).getPrincipal();

        String time = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("a h:mm").withLocale(Locale.KOREA));

        ChatMessageDto.Response response = ChatMessageDto.Response.builder()
                .userId(Long.valueOf(user.getUsername()))
                .username(user.getRealName())
                .profileImageUrl(user.getProfileImageUrl())
                .content(request.getContent())
                .time(time)
                .build();

        simpMessagingTemplate.convertAndSend("/topic/chat/" + roomId, response);
    }
    //나갔는지 들어왔는지 메시지 브로드캐스트
    public void sendStatusMessage(ChatUserStatusType chatUserStatusType, String roomId, String userId){
        String userStatusMessage = userService.findNameById(Long.valueOf(userId));
        if(chatUserStatusType == ChatUserStatusType.JOIN)
            userStatusMessage += "님이 채팅방에 참여하였습니다.";
        else
            userStatusMessage += "님이 채팅방에서 나가셨습니다.";

        simpMessagingTemplate.convertAndSend("/topic/chat/" + roomId, userStatusMessage);
    }

    //유저 참여시 유저정보 브로드캐스트
    public void sendUserInfo(){}
}
