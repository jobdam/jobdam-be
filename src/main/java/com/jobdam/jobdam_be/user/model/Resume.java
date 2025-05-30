package com.jobdam.jobdam_be.user.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Resume {
    private Long resumeId;
    private Long userId;
    private String url;
}
