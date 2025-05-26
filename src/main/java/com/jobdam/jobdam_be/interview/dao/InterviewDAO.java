package com.jobdam.jobdam_be.interview.dao;

import com.jobdam.jobdam_be.interview.mapper.InterviewMapper;
import com.jobdam.jobdam_be.interview.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class InterviewDAO {
    private final InterviewMapper interviewMapper;

    public List<InterviewJobJoinModel> findPagedInterviews(Long userId, Long lastId, int limit) {
        return interviewMapper.findPagedInterviews(userId,lastId, limit);
    }
    public List<Map<String, Object>> findFeedbackByInterviewIdAndUserId(Long interviewId, Long userId) {
        return interviewMapper.findFeedbackByInterviewIdAndUserId(interviewId, userId);
    }

    public int insertAiQuestions(List<AiResumeQuestion> questions) {
        return interviewMapper.insertAiQuestions(questions);
    }

    public void resetAiQuestion(Long resumeId) {
        interviewMapper.resetAiQuestion(resumeId);
    }

    public void saveInterview(Interview interview) {
         interviewMapper.saveInterview(interview);
    }

    public void copyAiToInterviewQuestions(Long userId, Long interviewId) {
        interviewMapper.copyAiToInterviewQuestions(userId, interviewId);
    }

    public List<InterviewQuestion> findAllLatestQuestionsByInterviewId(Long interviewId) {
        return interviewMapper.findAllLatestQuestionsByInterviewId(interviewId);
    }

    public void saveQuestion(InterviewQuestion interviewQuestion) {
        interviewMapper.saveQuestion(interviewQuestion);
    }

    public void saveFeedBack(FeedBack feedBack) {
        interviewMapper.saveFeedBack(feedBack);
    }
    public List<String> findFeedbacksForSameInterview(Long interviewId) {

        return interviewMapper.findFeedbacksForSameInterview(interviewId);
    }

    public void updateInterviewReports(Interview interview) {
        int updatedRows = interviewMapper.updateInterviewReports(interview);
        System.out.println("업데이트된 행 수: " + updatedRows); // 0이면 조건 불일치
    }

    public Optional<Interview> findOneLatestInterviewByUserId(Long userId) {
        return interviewMapper.findOneLatestInterviewByUserId(userId);
    }
}
