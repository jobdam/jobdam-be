package com.jobdam.jobdam_be.chat.dto;

import com.jobdam.jobdam_be.chat.type.ChatMessageType;
import lombok.Builder;
import lombok.Getter;

public class ChatReadyStatusDTO {
    @Getter
    public static class Request{
        private Boolean ready;
    }
    @Getter
    @Builder
    public static class Response{
        private ChatMessageType chatMessageType;
        private Long userId;
        private Boolean ready;
        private Boolean allReady;
    }
}
