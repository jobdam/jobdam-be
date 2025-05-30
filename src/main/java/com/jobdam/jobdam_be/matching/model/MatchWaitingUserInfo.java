package com.jobdam.jobdam_be.matching.model;

import com.jobdam.jobdam_be.matching.type.ExperienceType;
import com.jobdam.jobdam_be.matching.type.MatchType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class MatchWaitingUserInfo {
    private String sessionId;
    private Long userId;
    private String jobGroupCode; //필수 조건
    private MatchType matchType; //필수조건 (1:1,3~6명, none)
    private String jobDetailCode;//서브 조건
    private ExperienceType experienceType;//서브조건
    private boolean inProgress;//매칭상태체크(3명이상방에서 list에 3명담을건데 그떄 담겨져있는지 체크)
    private Instant joinedAt;//언제 참여했는지.
    private InterviewPreference interviewPreference;
}
