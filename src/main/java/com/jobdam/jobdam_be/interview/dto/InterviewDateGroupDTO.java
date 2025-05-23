package com.jobdam.jobdam_be.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class InterviewDateGroupDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String displayDate;
        private List<InterviewDTO.Response> interviews;
    }
}
