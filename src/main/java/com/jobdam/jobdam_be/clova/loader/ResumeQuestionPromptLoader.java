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
public class ResumeQuestionPromptLoader {
    @Getter
    private String resumeQuestionPrompt;

    @Value("${clova.resume.question.prompt}")
    private Resource promptFile;

    @PostConstruct
    public void loadPrompt() {
        try {
            resumeQuestionPrompt = Files.readString(Paths.get(promptFile.getURI()));
            log.info("자기소개서 질문 추출 프롬프트 로드 완료: {}", resumeQuestionPrompt);
        } catch (IOException e) {
            log.error("자기소개서 질문 추출 프롬프트 파일 로드 실패:  {}", e.getMessage(), e);
            resumeQuestionPrompt = "당신은 HR 면접관이자 채용 담당 역할을 수행하는 AI입니다.\n" +
                    "사용자가 입력한 자기소개서 또는 지원서를 분석하여 다음 기준에 따라 예상 면접 질문을 생성합니다.\n" +
                    "조건:\n" +
                    "1. 인성 질문 (5개): 성격, 가치관, 협업 경험, 갈등 해결 능력 등 인간적인 측면\n" +
                    "2. 기술 질문 (5개): 자소서에 언급된 기술 스택, 프로젝트 경험, 도구 활용 등 실무 역량 중심\n" +
                    "3. 직무 질문 (5개): 지원한 회사와 직무에 대한 관심, 직무 이해도, 성장 계획, 회사 기여 방안 등\n" +
                    "요구 사항:\n" +
                    "* 각 질문은 자소서 내용을 바탕으로 개인화된 형태여야 합니다. 단순한 일반 질문이 아니라 경험, 가치관, 기술 등을 기반으로 작성하세요.\n" +
                    "* 설명, 이모지, 라벨 없이 질문만 출력하세요.\n" +
                    "* 출력은 아래와 같은 JSON 형식을 반드시 따르세요.\n" +
                    "* JSON 이외의 어떠한 출력도 포함하지 마세요.\n" +
                    "* 모든 질문은 한 번에 출력되어야 하며, 도중에 잘리거나 빠지면 안 됩니다.\n" +
                    "형식 예시:\n" +
                    "{\n" +
                    "\"questions\": {\n" +
                    "\"인성\": [\n" +
                    "\"인성질문1\",\n" +
                    "\"인성질문2\",\n" +
                    "\"인성질문3\",\n" +
                    "\"인성질문4\",\n" +
                    "\"인성질문5\"\n" +
                    "],\n" +
                    "\"기술\": [\n" +
                    "\"기술질문1\",\n" +
                    "\"기술질문2\",\n" +
                    "\"기술질문3\",\n" +
                    "\"기술질문4\",\n" +
                    "\"기술질문5\"\n" +
                    "],\n" +
                    "\"직무\": [\n" +
                    "\"직무질문1\",\n" +
                    "\"직무질문2\",\n" +
                    "\"직무질문3\",\n" +
                    "\"직무질문4\",\n" +
                    "\"직무질문5\"\n" +
                    "]\n" +
                    "}\n" +
                    "}";
        }
    }
}

