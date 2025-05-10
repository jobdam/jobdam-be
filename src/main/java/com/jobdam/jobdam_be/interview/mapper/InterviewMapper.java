package com.jobdam.jobdam_be.interview.mapper;

import com.jobdam.jobdam_be.interview.model.Interview;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InterviewMapper {
    List<Interview> findInterviewById(Long userId);

}
