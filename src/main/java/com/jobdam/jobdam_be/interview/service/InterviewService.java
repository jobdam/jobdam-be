package com.jobdam.jobdam_be.interview.service;

import com.jobdam.jobdam_be.interview.dao.InterviewDAO;
import com.jobdam.jobdam_be.interview.dto.*;
import com.jobdam.jobdam_be.interview.exception.InterviewErrorCode;
import com.jobdam.jobdam_be.interview.exception.InterviewException;
import com.jobdam.jobdam_be.interview.model.AiResumeQuestion;
import com.jobdam.jobdam_be.interview.model.FeedBack;
import com.jobdam.jobdam_be.interview.model.Interview;
import com.jobdam.jobdam_be.interview.model.InterviewQuestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewDAO interviewDAO;
    private final ModelMapper modelMapper;

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

    //화상채팅에 들어가면 인터뷰 테이블을 생성하고
    //ai질문을 인터뷰테이블로 복사하고 질문을 가져오는 초기화함수
    @Transactional
    public VideoChatInterViewDTO.Response initInterview(Long userId, InterviewDTO.Request request) {
        //인터뷰 insert
        Interview interview = Interview.builder()
                .userId(userId)
                .interviewType(request.getInterviewType())
                .jobCode(request.getJobCode())
                .build();
        interviewDAO.saveInterview(interview);

        if(Objects.isNull(interview.getId()))
            throw new InterviewException(InterviewErrorCode.DB_INSERT_ERROR);

        //Ai질문을 인터뷰질문테이블로 복사
        interviewDAO.copyAiToInterviewQuestions(userId,interview.getId());
        //질문 가져오기
        List<InterviewQuestionDTO.Response> iqResponses = getInterviewQuestions(interview.getId());

        return VideoChatInterViewDTO.Response.builder()
               .interviewId(interview.getId())
               .interviewQuestions(iqResponses)
               .build();
    }
    //인터뷰질문들 조회
    public List<InterviewQuestionDTO.Response> getInterviewQuestions(Long interviewId) {
        return interviewDAO.findAllByInterviewId(interviewId)
                .stream()
                .map(iq  ->  modelMapper.map(iq ,InterviewQuestionDTO.Response.class))
                .toList();
    }

    //질문 저장
    @Transactional
    public Long saveQuestion(Long interviewId, InterviewQuestionDTO.Request request) {
        InterviewQuestion interviewQuestion = InterviewQuestion.builder()
                .interviewId(interviewId)
                .context(request.getContext())
                .build();
        interviewDAO.saveQuestion(interviewQuestion);
        if(Objects.isNull(interviewQuestion.getId()))
            throw new InterviewException(InterviewErrorCode.DB_INSERT_ERROR);

        return interviewQuestion.getId();
    }

    public void saveFeedBack(Long questionId, FeedBackDTO.Request request) {
        interviewDAO.saveFeedBack(FeedBack.builder()
                .targetUserId(request.getTargetUserId())
                .content(request.getContent())
                .interviewQuestionId(questionId)
                .build()
        );
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
