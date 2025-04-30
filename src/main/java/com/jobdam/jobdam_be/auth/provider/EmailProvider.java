package com.jobdam.jobdam_be.auth.provider;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;
    @Value("${spring.mail.properties.mail.smtp.nickname}")
    private String fromName;
    private final String SUBJECT = "[잡담 - 모의면접 매칭 서비스] 이메일 인증 메일입니다.";

    /**
     * POST /login 클라이언트가 로그인 POST 요청을 보낼 때 실행
     *
     * @param email 유저의 이메일 주소
     * @param code  인증번호
     * @return AuthenticationManager 내부에서  UserDetailsService를 통해 검사 -> 결과에 따라 성공, 실패 메서드 호출
     */
    @Async("mailExecutor")
    public CompletableFuture<Boolean> sendVerificationMail(String email, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = getVerificationMessage(code);

            messageHelper.setTo(email);     // 대상의 이메일 주소
            messageHelper.setSubject(SUBJECT);      // 제목
            messageHelper.setText(htmlContent, true);   // 내용
            messageHelper.setFrom(fromAddress, fromName);

            javaMailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.completedFuture(true);
    }

    private String getVerificationMessage(String code) {
        String verificationMessage = "";
        verificationMessage += "<h1 style='text-align:center;'> [잡담 - 모의면접 매칭 서비스] 인증 메일 </h1>";
        verificationMessage += "<h3 style='text-align:center;'> 인증코드 : <string style='font-size: 32px; letter-spacing: 8px;'>" + code + "</string></h3>";
        return verificationMessage;
    }
}
