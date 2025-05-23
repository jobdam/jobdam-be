package com.jobdam.jobdam_be.clova.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobdam.jobdam_be.clova.api.ClovaApiClient;
import com.jobdam.jobdam_be.clova.dto.Message;
import com.jobdam.jobdam_be.clova.dto.ChatRequest;
import com.jobdam.jobdam_be.clova.exception.ClovaErrorCode;
import com.jobdam.jobdam_be.clova.exception.ClovaException;
import com.jobdam.jobdam_be.clova.loader.FeedbackSamplingPromptLoader;
import com.jobdam.jobdam_be.clova.loader.ResumeQuestionPromptLoader;
import com.jobdam.jobdam_be.clova.loader.ResumeSamplingPromptLoader;
import com.jobdam.jobdam_be.common.PDFProvider;
import com.jobdam.jobdam_be.interview.model.AiResumeQuestion;
import com.jobdam.jobdam_be.interview.service.InterviewService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClovaAiService {
    private final ClovaApiClient clovaApiClient;
    private final InterviewService interviewService;

    private String questionPrompt;
    private String samplingResumePrompt;
    private String feedbackSamplingPrompt;

    private final ResumeSamplingPromptLoader resumeSummaryPromptLoader;
    private final ResumeQuestionPromptLoader resumeQuestionPromptLoader;
    private final FeedbackSamplingPromptLoader feedbackSummaryPromptLoader;

    @Value("${clova.resume.question.id}")
    private String questionId;
    @Value("${clova.resume.sampling.id}")
    private String samplingResumeId;
    @Value("${clova.feedback.sampling.id}")
    private String feedbackSamplingId;

    private static final int MIN_TEXT_LENGTH_FOR_SAMPLING = 500;

    @PostConstruct
    public void loadPrompts() {
        this.questionPrompt = resumeQuestionPromptLoader.getResumeQuestionPrompt();
        this.samplingResumePrompt = resumeSummaryPromptLoader.getResumeSummaryPrompt();
        this.feedbackSamplingPrompt = feedbackSummaryPromptLoader.getFeedbackSummaryPrompt();
        log.info("Clova 프롬프트 로드 완료");
        log.debug("Question Prompt: {}", questionPrompt);
        log.debug("Sampling Resume Prompt: {}", samplingResumePrompt);
        log.debug("Feedback Sampling Prompt: {}", feedbackSamplingPrompt);
    }

    @Async("clovaExecutor")
    public void analyzeResumeAndPDF(MultipartFile resume, Long resumeId) {
        String resumeText = PDFProvider.pdfToString(resume);
        if (resumeText.length() < MIN_TEXT_LENGTH_FOR_SAMPLING) {
            analyzeWithoutSampling(resumeText, resumeId);
        } else {
            analyzeWithSampling(resumeText, resumeId);
        }
        log.info("Clova Ai Success!!!!!!");
    }

    /**
     * 피드백 기반 리포트 추출
     *
     * @return aaaa
     */
    @Async("clovaExecutor")
    public CompletableFuture<List<String>> analyzeFeedback(String feedbackText) throws Exception {
        ChatRequest samplingRequest = new ChatRequest(
                List.of(new Message("system", feedbackSamplingPrompt), new Message("user", feedbackText)), 200);

        String samplingFeedback = "";
        samplingFeedback += clovaApiClient.samplingFeedBack(feedbackSamplingId, samplingRequest).block();
        List<String> reports = extractReports(samplingFeedback);
        if (reports.size() < 2)
            throw new ClovaException(ClovaErrorCode.AI_RESPONSE_INVALID);
        return CompletableFuture.completedFuture(reports);

        // 원하는 방식에 따라 직접 연결해도 괜찮음
        // return 타입만 CompletableFuture<List<String>> 에서 void로 변경 후
        // interviewService.insertFeedbackReport(/*인터뷰 아이디*/ 4L, reports);
    }

    // 내용이 적기에 요약이 필요하지 않을 경우
    private void analyzeWithoutSampling(String resumeText, Long resumeId) {
        String questions = buildQuestionMono(resumeText).block();
        saveQuestionsAsync(resumeId, questions);
    }

    // 내용이 많아 요약이 필요할 경우
    private void analyzeWithSampling(String resumeText, Long resumeId) {
        ChatRequest samplingRequest = new ChatRequest(
                List.of(new Message("system", samplingResumePrompt), new Message("user", resumeText)), 1000);

        String samplingResume = clovaApiClient.samplingResume(samplingResumeId, samplingRequest).block();
        String questions = buildQuestionMono(samplingResume).block();
        saveQuestionsAsync(resumeId, questions);
    }

    // 질문 생성
    private Mono<String> buildQuestionMono(String inputText) {
        ChatRequest chatRequest = new ChatRequest(
                List.of(new Message("system", questionPrompt), new Message("user", inputText)), 800);
        return clovaApiClient.generateQuestions(questionId, chatRequest);
    }

    // 저장 요청
    private void saveQuestionsAsync(Long resumeId, String response) {
        try {
            List<AiResumeQuestion> questions = extractQuestions(resumeId, response);
            interviewService.replaceAllAiQuestions(resumeId, questions);
        } catch (Exception e) {
            throw new ClovaException(ClovaErrorCode.AI_RESPONSE_PARSING_FAILED, e);
        }
    }

    // json 으로 받은 답변 쪼개기
    private List<AiResumeQuestion> extractQuestions(Long resumeId, String json) throws Exception {
        String cleanedJson = json.replaceAll("(?s)```(?:json)?\\s*|```", "").trim();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(cleanedJson);

        JsonNode questionsNode = root.get("questions");
        if (questionsNode == null || !questionsNode.isObject()) {
            throw new ClovaException(ClovaErrorCode.AI_RESPONSE_PARSING_FAILED);
        }

        List<AiResumeQuestion> questionList = new ArrayList<>();

        for (Iterator<String> it = questionsNode.fieldNames(); it.hasNext(); ) {
            String field = it.next();
            JsonNode arr = questionsNode.get(field);
            if (arr.isArray()) {
                for (JsonNode q : arr) {
                    questionList.add(AiResumeQuestion.builder()
                            .resumeId(resumeId)
                            .question(q.asText())
                            .build());
                }
            }
        }

        return questionList;
    }

    // json 으로 받은 답변 쪼개기
    private List<String> extractReports(String json) throws Exception {
        String cleanedJson = json.replaceAll("(?s)```(?:json)?\\s*|```", "").trim();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(cleanedJson);

        List<String> reportsList = new ArrayList<>();

        for (Iterator<String> it = root.fieldNames(); it.hasNext(); ) {
            String field = it.next();
            JsonNode arr = root.get(field);
            StringBuilder report = new StringBuilder();
            if (arr.isArray()) {
                for (JsonNode q : arr) {
                    report.append(q.asText()).append(" ");
                }
            }
            reportsList.add(String.valueOf(report));
        }

        return reportsList;
    }
}
