package com.jobdam.jobdam_be.auth.service;

import com.jobdam.jobdam_be.auth.dao.EmailVerificationDAO;
import com.jobdam.jobdam_be.auth.dao.RefreshDAO;
import com.jobdam.jobdam_be.auth.dto.CheckVerificationDto;
import com.jobdam.jobdam_be.auth.dto.EmailVerificationDto;
import com.jobdam.jobdam_be.auth.dto.SignUpDto;
import com.jobdam.jobdam_be.auth.exception.AuthException;
import com.jobdam.jobdam_be.auth.model.EmailVerification;
import com.jobdam.jobdam_be.auth.provider.EmailProvider;
import com.jobdam.jobdam_be.auth.provider.JwtProvider;
import com.jobdam.jobdam_be.common.VerificationCode;
import com.jobdam.jobdam_be.user.dao.UserDAO;
import com.jobdam.jobdam_be.user.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static com.jobdam.jobdam_be.auth.exception.AuthErrorCode.*;

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

    public ResponseEntity<String> emailVerification(EmailVerificationDto dto) {
        try {
            String email = dto.getEmail();
            boolean isExistId = userDAO.existsByEmail(email);
            if (isExistId) throw new AuthException(DUPLICATE_EMAIL);

            String verificationCode = VerificationCode.getVerificationCode();
            boolean isSuccess = emailProvider.sendVerificationMail(email, verificationCode);
            if (!isSuccess) throw new AuthException(MAIL_SEND_ERROR);

            EmailVerification verification = new EmailVerification(email,
                    verificationCode,
                    new Timestamp(System.currentTimeMillis()));

            verificationDAO.saveOrUpdateVerification(verification);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AuthException(DB_ERROR);

        }
        return ResponseEntity.ok().body("메일 전송 성공");
    }

    public ResponseEntity<String> checkVerification(CheckVerificationDto dto) {
        try {
            String email = dto.getEmail();
            String code = dto.getCode();

            EmailVerification emailVerification = verificationDAO.findByEmail(email);
            if (emailVerification == null) throw new AuthException(INVALID_EMAIL_OR_PASSWORD);

            boolean isMatched = emailVerification.getEmail().equals(email) && emailVerification.getCode().equals(code);
            if (!isMatched) throw new AuthException(INVALID_EMAIL_OR_PASSWORD);

        } catch (AuthException e) {
            log.error(e.getMessage(), e);
            throw new AuthException(e.getErrorCode());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AuthException(DB_ERROR);
        }

        return ResponseEntity.ok().body("메일 전송 성공");
    }

    public ResponseEntity<String> signUp(SignUpDto dto) {

        String email = dto.getEmail();
        String code = dto.getCode();

        boolean isExistId = userDAO.existsByEmail(email);
        if (isExistId) throw new AuthException(DUPLICATE_EMAIL);

        EmailVerification emailVerification = verificationDAO.findByEmail(email);

        if (emailVerification == null) throw new AuthException(EMAIL_VERIFICATION_REQUIRED);
        boolean isMatched = emailVerification.getEmail().equals(email) && emailVerification.getCode().equals(code);
        if (!isMatched) throw new AuthException(INVALID_EMAIL_OR_PASSWORD);

        String password = dto.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        dto.setPassword(encodedPassword);

        User user = new User(dto);

        boolean isSaved = userDAO.save(user);
        if(!isSaved) throw new AuthException(DB_ERROR);

        verificationDAO.deleteByEmail(email);

        return ResponseEntity.ok().body("메일 전송 성공");
    }
}
