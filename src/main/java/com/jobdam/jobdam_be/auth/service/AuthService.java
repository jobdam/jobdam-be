package com.jobdam.jobdam_be.auth.service;

import com.jobdam.jobdam_be.auth.dao.CertificationDAO;
import com.jobdam.jobdam_be.auth.dto.EmailCertificationRequestDto;
import com.jobdam.jobdam_be.auth.model.EmailVerification;
import com.jobdam.jobdam_be.auth.provider.EmailProvider;
import com.jobdam.jobdam_be.common.CertificationCode;
import com.jobdam.jobdam_be.user.dao.UserDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final CertificationDAO certificationDAO;
    private final EmailProvider emailProvider;

    private final UserDAO userDAO;

    public ResponseEntity<?> emailCertification(EmailCertificationRequestDto emailCertificationRequestDto) {
        try {
            String email = emailCertificationRequestDto.getEmail();
            boolean isExistId = userDAO.existsByEmail(email);
            if (isExistId) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이미 사용중인 이메일입니다.");


            String certificationCode = CertificationCode.getCertificationCode();
            boolean isSuccess = emailProvider.sendCertificationMail(email, certificationCode);
            if (!isSuccess) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 전송에 실패하였습니다.");

            EmailVerification certification = new EmailVerification(email,
                    certificationCode,
                    new Timestamp(System.currentTimeMillis()));

            certificationDAO.saveOrUpdateCertification(certification);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류 발생");
        }

        return ResponseEntity.ok().body("전송 성공");
    }
}
