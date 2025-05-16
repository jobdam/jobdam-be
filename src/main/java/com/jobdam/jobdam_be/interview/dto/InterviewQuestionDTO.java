package com.jobdam.jobdam_be.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class InterviewQuestionDTO {

    @Getter
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
