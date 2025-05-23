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
public class ResumeSamplingPromptLoader {
    @Getter
    private String resumeSummaryPrompt;

    @Value("${clova.resume.sampling.prompt}")
    private Resource promptFile;

    @PostConstruct
    public void loadPrompt() {
        try {
            resumeSummaryPrompt = Files.readString(Paths.get(promptFile.getURI()));
            log.info("자기소개서 요약 프롬프트 로드 완료: {}", resumeSummaryPrompt);
        } catch (IOException e) {
            log.error("자기소개서 요약 프롬프트 파일 로드 실패:  {}", e.getMessage(), e);
            resumeSummaryPrompt = "너는 사용자의 자기소개서를 면접 질문 생성을 위한 핵심 정보로 요약하는 역할이야.\n" +
                    "자기소개서가 길기 때문에, 다음 기준을 따르도록 해:\n" +
                    "- 문단 단위로 나눠 각 문단을 1~2줄로 요약\n" +
                    "- 각 문단의 핵심 행동, 결과, 사용 기술/태도 중심으로 요약\n" +
                    "- 총 요약 결과는 불릿포인트 형태로 작성\n" +
                    "- 불분명하거나 중복되는 내용은 제거\n" +
                    "- 면접 질문으로 이어질 수 있는 포인트 위주 (도전, 협업, 갈등 해결, 성취 등)\n" +
                    "- 질문 예시는 제거\n" +
                    "요약 결과는 다음과 같은 형식이어야 해:\n" +
                    "- [핵심 행동 및 경험] + [결과] + [기술/태도]";
        }
    }
}


