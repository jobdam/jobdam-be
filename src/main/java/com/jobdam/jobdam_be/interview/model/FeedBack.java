package com.jobdam.jobdam_be.interview.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedBack {
    private Long id;
    private Long targetUserId;
    private String content;
    private Long interviewQuestionId;
}
