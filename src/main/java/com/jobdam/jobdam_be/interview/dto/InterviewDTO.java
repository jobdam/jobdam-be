package com.jobdam.jobdam_be.interview.dto;

import com.jobdam.jobdam_be.interview.type.InterviewType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class InterviewDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request{
        private InterviewType interviewType;
        private String jobCode;
    }
}
