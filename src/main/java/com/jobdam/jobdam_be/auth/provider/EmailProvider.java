package com.jobdam.jobdam_be.auth.provider;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender javaMailSender;

    private final String SUBJECT = "[잡담 - 모의면접 매칭 서비스] 이메일 인증 메일입니다.";

    // TODO: 이메일 인증 비동기 처리
    /**
     * POST /login 클라이언트가 로그인 POST 요청을 보낼 때 실행
     *
     * @param email 유저의 이메일 주소
     * @param code 인증번호
     * @return AuthenticationManager 내부에서  UserDetailsService를 통해 검사 -> 결과에 따라 성공, 실패 메서드 호출
     */
    public boolean sendVerificationMail(String email, String code) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            String htmlContent = getVerificationMessage(code);

            messageHelper.setTo(email);     // 대상의 이메일 주소
            messageHelper.setSubject(SUBJECT);      // 제목
            messageHelper.setText(htmlContent, true);   // 내용

            javaMailSender.send(message);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }

        return true;
    }

    private String getVerificationMessage(String code) {
        String verificationMessage = "";
        verificationMessage += "<h1 style='text-align:center;'> [잡담 - 모의면접 매칭 서비스] 인증 메일 </h1>";
        verificationMessage += "<h3 style='text-align:center;'> 인증코드 : <string style='font-size: 32px; letter-spacing: 8px;'>" + code + "</string></h3>";
        return verificationMessage;
    }
}
