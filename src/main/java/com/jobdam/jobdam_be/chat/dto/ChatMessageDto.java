package com.jobdam.jobdam_be.chat.dto;

import com.jobdam.jobdam_be.chat.type.ChatMessageType;
import lombok.Builder;
import lombok.Getter;


public class ChatMessageDto {

    @Getter
    public static class Request{
        private String content;
    }

    @Getter
    @Builder
    public static class Response {
        private ChatMessageType chatMessageType;
        private Long userId;
        private String userName;
        private String profileImageUrl;
        private String content;
        private String time;
    }
}
