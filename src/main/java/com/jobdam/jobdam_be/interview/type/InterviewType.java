package com.jobdam.jobdam_be.interview.type;

import lombok.Getter;

@Getter
public enum InterviewType {
    PERSONALITY("인성"),
    JOB("직무"),
    TECHNICAL("기술");

    private final String displayName;

    InterviewType(String displayName) {
        this.displayName = displayName;
    }
}
