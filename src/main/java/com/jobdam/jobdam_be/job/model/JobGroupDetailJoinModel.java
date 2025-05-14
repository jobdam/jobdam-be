package com.jobdam.jobdam_be.job.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JobGroupDetailJoinModel {
    private String jobCode;
    private String jobGroup;
    private String jobDetailCode;
    private String jobDetail;
}
