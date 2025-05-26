package com.jobdam.jobdam_be.interview.service;

import com.jobdam.jobdam_be.interview.dao.InterviewDAO;
import com.jobdam.jobdam_be.interview.dto.*;
import com.jobdam.jobdam_be.interview.exception.InterviewErrorCode;
import com.jobdam.jobdam_be.interview.exception.InterviewException;
import com.jobdam.jobdam_be.interview.model.*;
import com.jobdam.jobdam_be.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewService {
    private final InterviewDAO interviewDAO;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public List<InterviewDateGroupDTO.Response> getInterviewsPaged(Long userId, Long lastId, int limit) {
        List<InterviewJobJoinModel> interviews = interviewDAO.findPagedInterviews(userId, lastId, limit);

        List<InterviewDTO.Response> interviewResponseList =
                interviews.stream().map((ijModel) -> {
                    InterviewDTO.Response dto = modelMapper.map(ijModel,InterviewDTO.Response.class);
                    dto.setInterviewDay(ijModel.getInterviewDay()
                            .toLocalDateTime()
                            .toLocalDate()
                            .toString());
                    return dto;
                }).toList();

        // 날짜별로 그룹핑
        Map<String, List<InterviewDTO.Response>> grouped =
                interviewResponseList.stream()
                        .collect(Collectors.groupingBy(InterviewDTO.Response::getInterviewDay));

        return  grouped.entrySet().stream()
                .sorted(Map.Entry.<String, List<InterviewDTO.Response>>comparingByKey().reversed())
                .map(entry -> InterviewDateGroupDTO.Response.builder()
                        .displayDate(formatDisplayDate(entry.getKey()))
                        .interviews(entry.getValue())
                        .build())
                .toList();
    }

    private String formatDisplayDate(String yyyyMMdd) {
        LocalDate date = LocalDate.parse(yyyyMMdd);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일 E요일", Locale.KOREAN);
        return date.format(formatter); // ex: "5월 21일 수요일"
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
        if (feedbacks.isEmpty()) {
            return null;
        }
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
    public void initInterview(Long userId, InterviewDTO.Request request) {
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
    }
    //화경면접 초기데이터 가져오기(이력서+ai질문들)
    public InterviewFullDataDTO.Response getInterviewFullData(Long userId) {
        String resumeUrl = userService.getUserResumeUrl(userId);

        Interview interview = interviewDAO.findOneLatestInterviewByUserId(userId)
                .orElseThrow( ()->new InterviewException(InterviewErrorCode.NOT_FOUND_EXCEPTION));

        List<InterviewQuestionDTO.Response> questions =  interviewDAO.findAllLatestQuestionsByInterviewId(interview.getId())
                .stream()
                .map(iq  ->  InterviewQuestionDTO.Response.builder()
                        .interviewQuestionId(iq.getId())
                        .context(iq.getContext())
                        .build())
                .toList();

        return InterviewFullDataDTO.Response.builder()
                .resumeUrl(resumeUrl)
                .interviewId(interview.getId())
                .interviewQuestions(questions)
                .build();
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

    @Transactional
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

    public Interview findOneLatestInterviewByUserId(Long userId){
        return interviewDAO.findOneLatestInterviewByUserId(userId)
                .orElseThrow( ()->new InterviewException(InterviewErrorCode.NOT_FOUND_EXCEPTION));
    }
}
