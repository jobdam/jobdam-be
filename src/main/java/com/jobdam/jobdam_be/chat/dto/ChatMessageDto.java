package com.jobdam.jobdam_be.chat.dto;

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
        private Long userId;
        private String username;
        private String profileImageUrl;
        private String content;
        private String time;
    }
}
