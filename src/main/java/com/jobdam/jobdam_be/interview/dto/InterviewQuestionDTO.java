package com.jobdam.jobdam_be.interview.dto;

import lombok.*;

public class InterviewQuestionDTO {

    @Data
    public static class Request{
        private String context;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private Long interviewQuestionId;
        private String context;
    }
}
