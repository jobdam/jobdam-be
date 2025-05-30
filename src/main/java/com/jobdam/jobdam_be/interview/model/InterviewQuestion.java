package com.jobdam.jobdam_be.interview.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InterviewQuestion {
    private Long id;
    private Long interviewId;
    private String context;
}
