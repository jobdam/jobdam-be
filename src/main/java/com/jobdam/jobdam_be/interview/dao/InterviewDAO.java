package com.jobdam.jobdam_be.interview.dao;

import com.jobdam.jobdam_be.interview.mapper.InterviewMapper;
import com.jobdam.jobdam_be.interview.model.AiResumeQuestion;
import com.jobdam.jobdam_be.interview.model.Interview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class InterviewDAO {
    private final InterviewMapper interviewMapper;

    public List<Interview> findInterviewById(Long userId) {
        return interviewMapper.findInterviewById(userId);
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

    public List<String> findFeedbacksForSameInterview(Long interviewId) {

        return interviewMapper.findFeedbacksForSameInterview(interviewId);
    }

    public void updateInterviewReports(Interview interview) {
        int updatedRows = interviewMapper.updateInterviewReports(interview);
        System.out.println("업데이트된 행 수: " + updatedRows); // 0이면 조건 불일치
    }
}
