package com.jobdam.jobdam_be.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserMatchingProfileDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String jobCode;
        private String jobDetailCode;
        private String experienceType;
    }
}
