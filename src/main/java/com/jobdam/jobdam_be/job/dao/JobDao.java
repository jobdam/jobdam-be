package com.jobdam.jobdam_be.job.dao;

import com.jobdam.jobdam_be.job.mapper.JobMapper;
import com.jobdam.jobdam_be.job.model.JobDetail;
import com.jobdam.jobdam_be.job.model.JobGroup;
import com.jobdam.jobdam_be.job.model.JobGroupDetailJoinModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JobDao {
    private final JobMapper jobMapper;

    public List<JobGroup> getAllJobGroups() {
        return jobMapper.getAllJobGroups();
    }

    public Collection<JobDetail> getJobDetailsByGroupCode(String jobCode) {
        return jobMapper.getJobDetailsByGroupCode(jobCode);
    }

    public JobGroupDetailJoinModel getJobGroupDetailJoinModel(String jobDetailCode){
     return jobMapper.getJobGroupDetailJoinModel(jobDetailCode);
    }

}
