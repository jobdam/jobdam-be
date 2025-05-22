package com.jobdam.jobdam_be.interview.dto;

import com.jobdam.jobdam_be.interview.type.InterviewType;
import lombok.*;

public class InterviewDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request{
        private InterviewType interviewType;
        private String jobCode;
    }
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private Long id;
        private InterviewType interviewType;
        private String interviewDay;
        private String jobName;
        private String wellDone;
        private String toImprove;
    }
}
