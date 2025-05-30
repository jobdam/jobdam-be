package com.jobdam.jobdam_be.chat.event;

import com.jobdam.jobdam_be.chat.dto.ChatStatusMessageDTO;
import com.jobdam.jobdam_be.chat.type.ChatMessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSessionEventListener {
    private final SimpMessagingTemplate simpMessagingTemplate;

    //채팅방을 나갔는지 들어왔는지 리스너
    @EventListener
    public void handleUserStatusChange(ChatSessionEvent event) {
        simpMessagingTemplate.convertAndSend("/topic/chat/" + event.roomId(),
                ChatStatusMessageDTO.Response.builder()
                        .chatMessageType(event.chatMessageType())
                        .userId(event.userId())
                        .userName(event.userName())
                        .build()
        );
        log.info("[채팅방 {}] userId={} username={}", event.chatMessageType(), event.userId(), event.userName());
    }

}
