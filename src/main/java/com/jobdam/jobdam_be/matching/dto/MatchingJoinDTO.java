package com.jobdam.jobdam_be.matching.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

public class MatchingJoinDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        private String roomId;
        private Date created;
        private boolean firstJoin;
    }
}
