package com.jobdam.jobdam_be.matching.model;

import com.jobdam.jobdam_be.matching.type.ExperienceType;
import com.jobdam.jobdam_be.matching.type.InterviewType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InterviewPreference {
    private Long userId;
    private String jobGroup;
    private String jobDetail;
    private ExperienceType experienceType;
    private String introducer;
    private InterviewType interviewType;
}
