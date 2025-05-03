package com.jobdam.jobdam_be.job.mapper;

import com.jobdam.jobdam_be.job.model.JobDetail;
import com.jobdam.jobdam_be.job.model.JobGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface JobMapper {
    List<JobGroup> getAllJobGroups();
    List<JobDetail> getJobDetailsByGroupCode(String jobCode);
}
