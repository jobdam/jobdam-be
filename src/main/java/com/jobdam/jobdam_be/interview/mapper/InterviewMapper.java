package com.jobdam.jobdam_be.interview.mapper;

import com.jobdam.jobdam_be.interview.model.Interview;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface InterviewMapper {
    List<Interview> findInterviewById(Long userId);

    List<Map<String, Object>> findFeedbackByInterviewIdAndUserId(Long interviewId, Long userId);
}
