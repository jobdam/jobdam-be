package com.jobdam.jobdam_be.matching.type;

import lombok.Getter;

@Getter
public enum ExperienceType {
    NEW("신입"),
    EXPERIENCED("경력");
    private final String displayName;

    ExperienceType(String displayName) {
        this.displayName = displayName;
    }
}
