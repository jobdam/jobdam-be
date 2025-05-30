package com.jobdam.jobdam_be.interview.model;

import com.jobdam.jobdam_be.interview.type.InterviewType;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AiResumeQuestion {
    private Long id;
    private Long resumeId;
    private String question;
    private InterviewType interviewType;
}
