package com.jobdam.jobdam_be.auth.service;

import com.jobdam.jobdam_be.auth.dao.EmailVerificationDAO;
import com.jobdam.jobdam_be.auth.dto.CheckVerificationDto;
import com.jobdam.jobdam_be.auth.dto.EmailVerificationDto;
import com.jobdam.jobdam_be.auth.dto.SignUpDto;
import com.jobdam.jobdam_be.auth.model.EmailVerification;
import com.jobdam.jobdam_be.auth.provider.EmailProvider;
import com.jobdam.jobdam_be.common.VerificationCode;
import com.jobdam.jobdam_be.user.dao.UserDAO;
import com.jobdam.jobdam_be.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailProvider emailProvider;
    private final PasswordEncoder passwordEncoder;

    private final EmailVerificationDAO verificationDAO;
    private final UserDAO userDAO;

    public ResponseEntity<Map<String, Boolean>> checkEmail(String email) {
        Map<String, Boolean> response = new HashMap<>();
        boolean isDuplicate = userDAO.existsByEmail(email);

        response.put("isDuplicate", isDuplicate);

        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<?> emailVerification(EmailVerificationDto dto) {
        try {
            String email = dto.getEmail();
            boolean isExistId = userDAO.existsByEmail(email);
            if (isExistId) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이미 사용 중인 이메일입니다.");


            String verificationCode = VerificationCode.getVerificationCode();
            boolean isSuccess = emailProvider.sendVerificationMail(email, verificationCode);
            if (!isSuccess) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 전송에 실패하였습니다.");

            EmailVerification verification = new EmailVerification(email,
                    verificationCode,
                    new Timestamp(System.currentTimeMillis()));

            verificationDAO.saveOrUpdateVerification(verification);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류가 발생했습니다.");
        }

        return ResponseEntity.ok().body("메일 전송 성공");
    }

    public ResponseEntity<?> checkVerification(CheckVerificationDto dto) {
        try {
            String email = dto.getEmail();
            String code = dto.getCode();

            EmailVerification emailVerification = verificationDAO.findByEmail(email);
            if (emailVerification == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 또는 비밀번호가 틀립니다.");

            boolean isMatched = emailVerification.getEmail().equals(email) && emailVerification.getCode().equals(code);
            if (!isMatched) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 또는 비밀번호가 틀립니다.");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("데이터베이스 오류가 발생했습니다.");
        }

        return ResponseEntity.ok().body("성공");
    }

    public ResponseEntity<?> signUp(SignUpDto dto) {

        String email = dto.getEmail();
        boolean isExistId = userDAO.existsByEmail(email);
        if (isExistId) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이미 사용 중인 이메일입니다.");

        String code = dto.getCode();

        EmailVerification emailVerification = verificationDAO.findByEmail(email);

        if (emailVerification == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일 인증을 먼저 진행하세요.");
        boolean isMatched = emailVerification.getEmail().equals(email) && emailVerification.getCode().equals(code);
        if (!isMatched) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("검증 오류 발생");

        String password = dto.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        dto.setPassword(encodedPassword);

        User user = new User(dto);

        boolean isSaved = userDAO.save(user);
        if(!isSaved) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("데이터베이스 오류가 발생했습니다.");

        verificationDAO.deleteByEmail(email);

        return ResponseEntity.ok().body("회원가입 성공");
    }
}
