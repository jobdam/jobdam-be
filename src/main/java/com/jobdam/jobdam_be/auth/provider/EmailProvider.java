package com.jobdam.jobdam_be.auth.provider;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender javaMailSender;

    private final TemplateEngine templateEngine;  // Thymeleaf
    @Value("${frontEnd.url}")
    private String frontUrl;

    @Value("${spring.mail.username}")
    private String fromAddress;
    @Value("${spring.mail.properties.mail.smtp.nickname}")
    private String fromName;
    private final String SUBJECT = "[잡담 - 모의면접 매칭 서비스] 이메일 인증 메일입니다.";

    /**
     * POST /login 클라이언트가 로그인 POST 요청을 보낼 때 실행
     *
     * @param email 유저의 이메일 주소
     * @param token  인증번호
     * @return AuthenticationManager 내부에서  UserDetailsService를 통해 검사 -> 결과에 따라 성공, 실패 메서드 호출
     */
    @Async("mailExecutor")
    public CompletableFuture<Boolean> sendVerificationMail(String email, String token) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            // 1. 이메일 콘텐츠 생성 (템플릿 렌더링)
            Context context = new Context();
            context.setVariable("email", email);
            context.setVariable("token", token);
            context.setVariable("frontEnd", frontUrl);
            String htmlContent = templateEngine.process("email-verification", context); // templates/email-verification.html

            messageHelper.setTo(email);     // 대상의 이메일 주소
            messageHelper.setSubject(SUBJECT);      // 제목
            messageHelper.setText(htmlContent, true);   // 내용
            messageHelper.setFrom(fromAddress, fromName);   // 별명 추가

            File logo = new File("src/main/resources/static/images/logo.png");
            File jobdam = new File("src/main/resources/static/images/jobdam.png");
            messageHelper.addInline("logo", logo);
            messageHelper.addInline("jobdam", jobdam);

            javaMailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.completedFuture(true);
    }
}
