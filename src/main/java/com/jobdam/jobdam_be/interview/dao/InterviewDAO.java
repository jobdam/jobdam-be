package com.jobdam.jobdam_be.interview.dao;

import com.jobdam.jobdam_be.interview.mapper.InterviewMapper;
import com.jobdam.jobdam_be.interview.model.Interview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class InterviewDAO {
    private final InterviewMapper interviewMapper;

    public List<Interview> findInterviewById(Long userId) {
        return interviewMapper.findInterviewById(userId);
    }

}
