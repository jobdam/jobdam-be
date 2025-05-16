package com.jobdam.jobdam_be.interview.dto;

import com.jobdam.jobdam_be.interview.type.InterviewType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class InterviewDTO {

    @Getter
    public static class Request{
        private InterviewType interviewType;
        private String jobCode;
    }
}
