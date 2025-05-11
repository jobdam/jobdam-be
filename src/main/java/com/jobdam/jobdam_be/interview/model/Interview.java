package com.jobdam.jobdam_be.interview.model;

import lombok.*;

import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Interview {
    private int id;
    private int userId;
    private String interviewType;
    private Timestamp interviewDay;
    private String jobCode;
}
