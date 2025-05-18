package com.jobdam.jobdam_be.chat.controller;

import com.jobdam.jobdam_be.auth.service.CustomUserDetails;
import com.jobdam.jobdam_be.chat.dto.ChatMessageDto;
import com.jobdam.jobdam_be.chat.dto.ChatReadyStatusDTO;
import com.jobdam.jobdam_be.chat.dto.ChatStatusMessageDTO;
import com.jobdam.jobdam_be.chat.storage.ChatRoomStore;
import com.jobdam.jobdam_be.chat.type.ChatMessageType;
import com.jobdam.jobdam_be.websokect.sessionTracker.domain.ChatSessionTracker;
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
    private final ChatRoomStore chatRoomStore;

    //채팅 보내기
    @MessageMapping("/chat/send/{roomId}")
    public void sendChat(@DestinationVariable String roomId,
                         ChatMessageDto.Request request,
                         Principal principal) {

        CustomUserDetails user = (CustomUserDetails) ((Authentication) principal).getPrincipal();

        String time = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("a h:mm").withLocale(Locale.KOREA));

        ChatMessageDto.Response response = ChatMessageDto.Response.builder()
                .chatMessageType(ChatMessageType.CHAT)
                .userId(Long.valueOf(user.getUsername()))
                .userName(user.getRealName())
                .profileImageUrl(user.getProfileImageUrl())
                .content(request.getContent())
                .time(time)
                .build();

        simpMessagingTemplate.convertAndSend("/topic/chat/" + roomId, response);
    }

    //화상채팅 들어가기 위한 Ready확인
    @MessageMapping("/chat/ready/{roomId}")
    public void handleReady(@DestinationVariable String roomId, Principal principal
            ,ChatReadyStatusDTO.Request request) {
        Long userId = Long.valueOf(principal.getName());
        chatRoomStore.markReady(roomId, userId, request.getReady());

        //준비완료면 전체가 준비됐는지 확인해서 화상채팅방으로
        boolean allReady = chatRoomStore.isAllReady(roomId);

        ChatReadyStatusDTO.Response response = ChatReadyStatusDTO.Response.builder()
                .chatMessageType(ChatMessageType.READY)
                .userId(userId)
                .ready(request.getReady())
                .allReady(allReady)
                .build();

        simpMessagingTemplate.convertAndSend("/topic/chat/" + roomId,response);
    }

    //화상면접방에서 채팅 보내기
    @MessageMapping("/video/chat/send/{roomId}")
    public void sendChatInVideo(@DestinationVariable String roomId,
                         ChatMessageDto.Request request,
                         Principal principal) {

        CustomUserDetails user = (CustomUserDetails) ((Authentication) principal).getPrincipal();

        ChatMessageDto.Response response = ChatMessageDto.Response.builder()
                .userId(Long.valueOf(user.getUsername()))
                .userName(user.getRealName())
                .content(request.getContent())
                .build();

        simpMessagingTemplate.convertAndSend("/topic/videoChat/" + roomId, response);
    }
}
