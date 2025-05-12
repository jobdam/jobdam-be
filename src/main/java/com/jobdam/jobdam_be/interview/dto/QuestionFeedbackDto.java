package com.jobdam.jobdam_be.interview.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionFeedbackDto {
    private Long questionId;
    private String question;
    private List<String> feedbacks;
}
