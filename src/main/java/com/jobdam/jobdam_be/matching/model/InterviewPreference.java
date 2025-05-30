package com.jobdam.jobdam_be.matching.model;

import com.jobdam.jobdam_be.matching.type.ExperienceType;
import com.jobdam.jobdam_be.interview.type.InterviewType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InterviewPreference {
    private Long userId;
    private String jobGroupCode;
    private String jobDetailCode;
    private ExperienceType experienceType;
    private String introduce;
    private InterviewType interviewType;
}
