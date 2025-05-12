package com.jobdam.jobdam_be.chat.controller;

import com.jobdam.jobdam_be.auth.service.CustomUserDetails;
import com.jobdam.jobdam_be.chat.dto.ChatMessageDto;
import com.jobdam.jobdam_be.user.model.User;
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
}
