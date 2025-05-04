package com.jobdam.jobdam_be.job.controller;

import com.jobdam.jobdam_be.job.dto.JobGroupDTO;
import com.jobdam.jobdam_be.job.service.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;

    @GetMapping("/jobs")
    public List<JobGroupDTO> getJobGroups() {
        return jobService.getAllJobGroupsWithDetails();
    }
}
