package com.jobdam.jobdam_be.interview.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AiResumeQuestion {
    private Long id;
    private Long resumeId;
    private String question;
}
