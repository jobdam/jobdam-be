package com.jobdam.jobdam_be.interview.dto;

import lombok.Data;

public class FeedBackDTO {

    @Data
    public static class Request{
        private long targetUserId;
        private String content;
    }
}
