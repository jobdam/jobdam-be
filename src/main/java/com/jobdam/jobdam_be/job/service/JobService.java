package com.jobdam.jobdam_be.job.service;

import com.jobdam.jobdam_be.job.dao.JobDao;
import com.jobdam.jobdam_be.job.dto.JobDetailDto;
import com.jobdam.jobdam_be.job.dto.JobGroupDto;
import com.jobdam.jobdam_be.job.model.JobGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {
    private final JobDao jobDao;

    public List<JobGroupDto> getAllJobGroupsWithDetails() {
        List<JobGroup> groups = jobDao.getAllJobGroups();
        return groups.stream().map(group -> {
            List<JobDetailDto> details = jobDao.getJobDetailsByGroupCode(group.getJobCode())
                    .stream()
                    .map(detail -> {
                        JobDetailDto dto = new JobDetailDto();
                        dto.setJobDetailCode(detail.getJobDetailCode());
                        dto.setJobDetail(detail.getJobDetail());
                        return dto;
                    })
                    .collect(Collectors.toList());

            JobGroupDto dto = new JobGroupDto();
            dto.setJobCode(group.getJobCode());
            dto.setJobGroup(group.getJobGroup());
            dto.setDetails(details);
            return dto;
        }).collect(Collectors.toList());
    }
}
