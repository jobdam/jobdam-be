package com.jobdam.jobdam_be.clova.loader;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Component
public class FeedbackSamplingPromptLoader {
    @Getter
    private String feedbackSummaryPrompt;

    @Value("${clova.feedback.sampling.prompt}")
    private Resource promptFile;

    @PostConstruct
    public void loadPrompt() {
        try {
            feedbackSummaryPrompt = Files.readString(Paths.get(promptFile.getURI()));
            log.info("피드백 요약 프롬프트 로드 완료: {}", feedbackSummaryPrompt);
        } catch (IOException e) {
            log.error("피드백 요약 프롬프트 파일 로드 실패:  {}", e.getMessage(), e);
            feedbackSummaryPrompt = "다음은 여러 면접관에게 받은 모의 면접 피드백 내용입니다. 피드백을 종합해 JSON 형식으로 정리해줘.\n" +
                    "각 항목은 다음 조건을 따라 작성해줘:\n" +
                    "- 잘한 점(2개)과 개선할 점(2개) 각각 2개 항목으로 요약\n" +
                    "- 내용이 중복되거나 유사하면 묶어서 핵심만 간결하게 정리\n" +
                    "- 최종 출력은 아래와 같은 JSON 형식으로 고정\n" +
                    "{\n" +
                    "\"well_done\": [\n" +
                    "\"잘한 점\",\n" +
                    "\"잘한 점\"\n" +
                    "],\n" +
                    "\"to_improve\": [\n" +
                    "\"개선할 점\",\n" +
                    "\"개선할 점\"\n" +
                    "]\n" +
                    "}";
        }
    }
}
