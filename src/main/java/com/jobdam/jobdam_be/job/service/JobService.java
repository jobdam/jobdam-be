package com.jobdam.jobdam_be.job.service;

import com.jobdam.jobdam_be.job.dao.JobDao;
import com.jobdam.jobdam_be.job.dto.JobDetailDTO;
import com.jobdam.jobdam_be.job.dto.JobGroupDTO;
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

    public List<JobGroupDTO> getAllJobGroupsWithDetails() {
        List<JobGroup> groups = jobDao.getAllJobGroups();
        return groups.stream().map(group -> {
            List<JobDetailDTO> details = jobDao.getJobDetailsByGroupCode(group.getJobCode())
                    .stream()
                    .map(detail -> {
                        JobDetailDTO dto = new JobDetailDTO();
                        dto.setJobDetailCode(detail.getJobDetailCode());
                        dto.setJobDetail(detail.getJobDetail());
                        return dto;
                    })
                    .collect(Collectors.toList());

            JobGroupDTO dto = new JobGroupDTO();
            dto.setJobCode(group.getJobCode());
            dto.setJobGroup(group.getJobGroup());
            dto.setDetails(details);
            return dto;
        }).collect(Collectors.toList());
    }
}
