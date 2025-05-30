package com.jobdam.jobdam_be.interview.mapper;

import com.jobdam.jobdam_be.interview.model.*;
import com.jobdam.jobdam_be.interview.type.InterviewType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface InterviewMapper {
    List<InterviewJobJoinModel> findPagedInterviews(Long userId, Long lastId, int limit);

    List<Map<String, Object>> findFeedbackByInterviewIdAndUserId(Long interviewId, Long userId);

    int insertAiQuestions(List<AiResumeQuestion> questions);

    void resetAiQuestion(Long resumeId);

    void saveInterview(Interview interview);

    void copyAiToInterviewQuestions(Long userId, Long interviewId, InterviewType interviewType);

    List<InterviewQuestion> findAllLatestQuestionsByInterviewId(Long interviewId);

    void saveQuestion(InterviewQuestion interviewQuestion);

    void saveFeedBack(FeedBack feedBack);

    List<String> findFeedbacksForSameInterview(Long interviewId);

    int updateInterviewReports(Interview interview);

    Optional<Interview> findOneLatestInterviewByUserId(Long userId);

}
