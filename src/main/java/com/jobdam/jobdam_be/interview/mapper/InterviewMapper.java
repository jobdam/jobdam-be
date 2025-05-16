package com.jobdam.jobdam_be.interview.mapper;

import com.jobdam.jobdam_be.interview.model.AiResumeQuestion;
import com.jobdam.jobdam_be.interview.model.FeedBack;
import com.jobdam.jobdam_be.interview.model.Interview;
import com.jobdam.jobdam_be.interview.model.InterviewQuestion;
import com.jobdam.jobdam_be.interview.type.InterviewType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface InterviewMapper {
    List<Interview> findInterviewById(Long userId);

    List<Map<String, Object>> findFeedbackByInterviewIdAndUserId(Long interviewId, Long userId);

    int insertAiQuestions(List<AiResumeQuestion> questions);

    void resetAiQuestion(Long resumeId);

    void saveInterview(Interview interview);

    void copyAiToInterviewQuestions(Long userId, Long interviewId);

    List<InterviewQuestion> findAllByInterviewId(Long interviewId);

    void saveQuestion(InterviewQuestion interviewQuestion);

    void saveFeedBack(FeedBack feedBack);
}
