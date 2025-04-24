package com.jobdam.jobdam_be.auth.service;

import com.jobdam.jobdam_be.auth.dao.EmailVerificationDAO;
import com.jobdam.jobdam_be.auth.dto.CheckVerificationRequestDto;
import com.jobdam.jobdam_be.auth.dto.EmailVerificationRequestDto;
import com.jobdam.jobdam_be.auth.model.EmailVerification;
import com.jobdam.jobdam_be.auth.provider.EmailProvider;
import com.jobdam.jobdam_be.common.VerificationCode;
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
    private final EmailVerificationDAO verificationDAO;
    private final EmailProvider emailProvider;

    private final UserDAO userDAO;

    public ResponseEntity<?> emailVerification(EmailVerificationRequestDto dto) {
        try {
            String email = dto.getEmail();
            boolean isExistId = userDAO.existsByEmail(email);
            if (isExistId) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이미 사용중인 이메일입니다.");


            String verificationCode = VerificationCode.getVerificationCode();
            boolean isSuccess = emailProvider.sendVerificationMail(email, verificationCode);
            if (!isSuccess) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 전송에 실패하였습니다.");

            EmailVerification verification = new EmailVerification(email,
                    verificationCode,
                    new Timestamp(System.currentTimeMillis()));

            verificationDAO.saveOrUpdateVerification(verification);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류 발생");
        }

        return ResponseEntity.ok().body("성공");
    }

    public ResponseEntity<?> checkVerification(CheckVerificationRequestDto dto) {
        try {
            String email = dto.getEmail();
            String code = dto.getCode();

            EmailVerification emailVerification = verificationDAO.findByEmail(email);
            if (emailVerification == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("검증 오류 발생");

            boolean isMatched = emailVerification.getEmail().equals(email) && emailVerification.getCode().equals(code);
            if (!isMatched) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("검증 오류 발생");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류 발생");
        }

        return ResponseEntity.ok().body("성공");
    }
}
