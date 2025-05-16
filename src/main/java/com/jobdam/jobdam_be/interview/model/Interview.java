package com.jobdam.jobdam_be.interview.model;

import com.jobdam.jobdam_be.interview.type.InterviewType;
import lombok.*;

import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {
    private Long id;
    private Long userId;
    private InterviewType interviewType;
    private Timestamp interviewDay;
    private String jobCode;
    private String wellDone;
    private String toImprove;
}
