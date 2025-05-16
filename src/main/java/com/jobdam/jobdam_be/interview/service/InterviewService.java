package com.jobdam.jobdam_be.interview.service;

import com.jobdam.jobdam_be.interview.dao.InterviewDAO;
import com.jobdam.jobdam_be.interview.dto.QuestionFeedbackDto;
import com.jobdam.jobdam_be.interview.model.AiResumeQuestion;
import com.jobdam.jobdam_be.interview.model.Interview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewDAO interviewDAO;

    public Map<String, List<Interview>> getInterview(Long userId) {
        List<Interview> interviews = interviewDAO.findInterviewById(userId);

        return interviews.stream()
                .collect(Collectors.groupingBy(interview -> {
                    Timestamp ts = interview.getInterviewDay(); // Timestamp
                    return ts.toLocalDateTime().toLocalDate().toString(); // "YYYY-MM-DD"
                }));
    }

    public List<QuestionFeedbackDto> getFeedbackHistory(Long interviewId, Long userId) {
        List<Map<String, Object>> rows = interviewDAO.findFeedbackByInterviewIdAndUserId(interviewId, userId);

        Map<Long, QuestionFeedbackDto> grouped = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            Long qid = ((Number) row.get("questionId")).longValue();
            String question = (String) row.get("question");
            String feedback = (String) row.get("feedback");

            // qid가 있는지 확인해서 있다면 grouped에 존재하는 qid 에 dto 추가
            // 없다면 grouped에 qid를 만들고 dto 추가
            grouped.computeIfAbsent(qid, id -> {
                QuestionFeedbackDto dto = new QuestionFeedbackDto();
                dto.setQuestionId(id);
                dto.setQuestion(question);
                dto.setFeedbacks(new ArrayList<>());
                return dto;
            }).getFeedbacks().add(feedback);
        }

        return new ArrayList<>(grouped.values());
    }

    public String getFeedbacksForSameInterview(Long interviewId) {
        List<String> feedbacks = interviewDAO.findFeedbacksForSameInterview(interviewId);
        return feedbacks.toString();
    }

    @Transactional
    public void replaceAllAiQuestions(Long resumeId, List<AiResumeQuestion> questions) {
        interviewDAO.resetAiQuestion(resumeId);
        int result = interviewDAO.insertAiQuestions(questions);
    }

    @Transactional
    public void insertFeedbackReport(Long interviewId, List<String> reports) {
        String wellDone = reports.get(0);
        String toImprove = reports.get(1);

        Interview interview = Interview.builder()
                .id(interviewId)
                .wellDone(wellDone)
                .toImprove(toImprove)
                .build();

        log.info("interview: {}", interview);
        interviewDAO.updateInterviewReports(interview);
    }
}
