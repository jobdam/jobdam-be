package com.jobdam.jobdam_be.matching.type;

import lombok.Getter;

@Getter
public enum MatchType {
    ONE_TO_ONE("1:1"),
    GROUP("3~6명"),
    NONE("미선택");

    private final String displayName;

    MatchType(String displayName) {
        this.displayName = displayName;
    }
}
