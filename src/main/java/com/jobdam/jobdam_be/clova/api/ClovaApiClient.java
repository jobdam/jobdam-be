package com.jobdam.jobdam_be.clova.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobdam.jobdam_be.clova.dto.ChatRequest;
import com.jobdam.jobdam_be.global.exception.type.CommonErrorCode;
import com.jobdam.jobdam_be.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClovaApiClient {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${clova.api.key}")
    private String apiKey;

    // 요약 AI 요청
    public Mono<String> samplingResume(String requestId, ChatRequest request) {
        return webClient.post()
                .uri("testapp/v3/chat-completions/HCX-005")
                .header("Authorization", "Bearer " + apiKey)
                .header("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(line -> log.info("RECV LINE: {}", line))
                .filter(line -> line.contains("\"result\""))
                .map(this::extractSamplingContent)
                .last();
    }

    // 질문 생성 AI 요청
    public Mono<String> generateQuestions(String requestId, ChatRequest request) {
        return webClient.post()
                .uri("/testapp/v3/chat-completions/HCX-DASH-002")
                .header("Authorization", "Bearer " + apiKey)
                .header("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(line -> !line.contains("[DONE]") && line.contains("\"message\""))
                .map(this::extractQuestionContent)
                .last();
    }
    // 질문 생성 AI 요청
    public Mono<String> samplingFeedBack(String requestId, ChatRequest request) {
        return webClient.post()
                .uri("/testapp/v3/chat-completions/HCX-DASH-002")
                .header("Authorization", "Bearer " + apiKey)
                .header("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
                .header("Content-Type", "application/json")
                .header("Accept", "text/event-stream")
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(String.class)
                .filter(line -> !line.contains("[DONE]") && line.contains("\"message\""))
                .map(this::extractQuestionContent)
                .last();
    }

    // 샘플링 결과 추출
    private String extractSamplingContent(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            return node.path("result").path("message").path("content").asText();
        } catch (Exception e) {
            throw new UserException(CommonErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    // 질문 생성 결과 추출
    private String extractQuestionContent(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            return node.path("message").path("content").asText();
        } catch (Exception e) {
            throw new UserException(CommonErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }
}
