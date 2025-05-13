package com.jobdam.jobdam_be.chat.dto;

import com.jobdam.jobdam_be.chat.type.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

public class ChatStatusMessageDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private ChatMessageType chatMessageType;
        private Long userId;
        private String userName;
    }
}
