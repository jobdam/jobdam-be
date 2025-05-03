package com.jobdam.jobdam_be.job.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobGroupDto {
    private String jobCode;
    private String jobGroup;
    private List<JobDetailDto> details;
}
