package com.jobdam.jobdam_be.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class InterviewFullDataDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
       private String resumeUrl;
       private List<InterviewQuestionDTO.Response> interviewQuestions;
    }
}
