package com.jobdam.jobdam_be.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MatchingJoinDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        private String roomId;
        private boolean isFirstJoin;
    }
}
