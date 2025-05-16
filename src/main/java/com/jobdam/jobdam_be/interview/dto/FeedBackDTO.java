package com.jobdam.jobdam_be.interview.dto;

import lombok.Getter;

public class FeedBackDTO {

    @Getter
    public static class Request{
        private long targetUserId;
        private String content;
    }
}
