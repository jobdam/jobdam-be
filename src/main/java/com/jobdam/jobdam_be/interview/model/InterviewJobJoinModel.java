package com.jobdam.jobdam_be.interview.model;

import com.jobdam.jobdam_be.interview.type.InterviewType;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewJobJoinModel {
    private Long id;
    private InterviewType interviewType;
    private Timestamp interviewDay;
    private String jobName;
    private String wellDone;
    private String toImprove;
}
