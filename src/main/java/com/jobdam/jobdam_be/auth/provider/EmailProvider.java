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

            String htmlContent = getVerificationMessage(email, code);

            messageHelper.setTo(email);     // 대상의 이메일 주소
            messageHelper.setSubject(SUBJECT);      // 제목
            messageHelper.setText(htmlContent, true);   // 내용
            messageHelper.setFrom(fromAddress, fromName);   // 별명 추가

            javaMailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.completedFuture(true);
    }

    private String getVerificationMessage(String email, String token) {
        String verificationUrl = "http://localhost:8080/verify?token=" + token;

        return """
                    <html>
                    <body style="font-family: Arial, sans-serif;">
                        <div style="max-width: 500px; margin: auto; padding: 20px; border: 1px solid #eee; border-radius: 10px;">
                            <h2 style="color: #2c3e50;">[잡담 - 모의면접 매칭 서비스] 이메일 인증 요청</h2>
                            <p>안녕하세요, %s님</p>
                            <p>아래 버튼을 클릭해 이메일 인증을 완료해주세요:</p>
                            <div style="text-align: center; margin: 20px 0;">
                                <a href="%s" style="
                                    display: inline-block;
                                    padding: 10px 20px;
                                    background-color: #3498db;
                                    color: white;
                                    text-decoration: none;
                                    border-radius: 5px;
                                    font-weight: bold;">
                                    이메일 인증하기
                                </a>
                            </div>
                            <p>감사합니다.</p>
                            <p style="font-size: 0.8em; color: gray;">이 메일은 인증 요청을 위해 발송되었습니다.</p>
                        </div>
                    </body>
                    </html>
                """.formatted(email, verificationUrl);
    }
}
