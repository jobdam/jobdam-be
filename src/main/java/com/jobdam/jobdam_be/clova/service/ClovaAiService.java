package com.jobdam.jobdam_be.clova.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobdam.jobdam_be.clova.api.ClovaApiClient;
import com.jobdam.jobdam_be.clova.dto.Message;
import com.jobdam.jobdam_be.clova.dto.ChatRequest;
import com.jobdam.jobdam_be.common.PDFProvider;
import com.jobdam.jobdam_be.global.exception.type.CommonErrorCode;
import com.jobdam.jobdam_be.interview.model.AiResumeQuestion;
import com.jobdam.jobdam_be.interview.service.InterviewService;
import com.jobdam.jobdam_be.user.exception.UserException;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class ClovaAiService {
    private final ClovaApiClient clovaApiClient;
    private final InterviewService interviewService;

    @Value("${clova.resume.question.prompt}")
    private String questionPrompt;
    @Value("${clova.resume.sampling.prompt}")
    private String samplingPrompt;

    @Value("${clova.resume.sampling.id}")
    private String samplingId;
    private String samplingResumePrompt;

    @Value("${clova.resume.question.id}")
    private String questionId;
    @Value("${clova.resume.sampling.id}")
    private String samplingResumeId;

    private static final int MIN_TEXT_LENGTH_FOR_SAMPLING = 500;

    @Async("clovaExecutor")
    public void analyzeResumeAndPDF(MultipartFile resume, Long resumeId) {
        String resumeText = PDFProvider.pdfToString(resume);

        if (resumeText.length() < MIN_TEXT_LENGTH_FOR_SAMPLING) {
            analyzeWithoutSampling(resumeText, resumeId);
        } else {
            analyzeWithSampling(resumeText, resumeId);
        }
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
            log.error("질문 분할 실패: {}", e.getMessage(), e);
        }
    }

    // json 으로 받은 답변 쪼개기
    private List<AiResumeQuestion> extractQuestions(Long resumeId, String json) throws Exception {
        String cleanedJson = json.replaceAll("(?s)```(?:json)?\\s*|```", "").trim();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(cleanedJson);

        JsonNode questionsNode = root.get("questions");
        if (questionsNode == null || !questionsNode.isObject()) {
            throw new UserException(CommonErrorCode.INTERNAL_SERVER_ERROR);
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
}
