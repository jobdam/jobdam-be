package com.jobdam.jobdam_be.chat.controller;

import com.jobdam.jobdam_be.chat.dto.ChatMessageDto;
import com.jobdam.jobdam_be.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWSMessageController {

    @MessageMapping("/chat/send/{roomId}")
    public void sendChat(@DestinationVariable String roomId,
                         ChatMessageDto.Request request,
                         Principal principal) {

//        Long userId = Long.parseLong(principal.getName());
//        User user = userRepository.findById(userId).orElseThrow();
//
//        String time = LocalTime.now()
//                .format(DateTimeFormatter.ofPattern("a h:mm").withLocale(Locale.KOREA));
//
//        ChatMessageDto.Response response = ChatMessageDto.Response.builder()
//                .userId(userId)
//                .username(user.getName())
//                .profileImageUrl(user.getProfileImageUrl())
//                .content(request.getContent())
//                .time(time)
//                .build();
//
//        simpMessagingTemplate.convertAndSend("/topic/chat/" + roomId, response);
    }
}
