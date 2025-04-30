package com.jobdam.jobdam_be.auth.service;

import com.jobdam.jobdam_be.auth.dao.EmailVerificationDAO;
import com.jobdam.jobdam_be.auth.dao.RefreshDAO;
import com.jobdam.jobdam_be.auth.dto.ResendDto;
import com.jobdam.jobdam_be.auth.dto.SignUpDto;
import com.jobdam.jobdam_be.auth.exception.AuthException;
import com.jobdam.jobdam_be.auth.model.EmailVerification;
import com.jobdam.jobdam_be.auth.provider.EmailProvider;
import com.jobdam.jobdam_be.auth.provider.JwtProvider;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.jobdam.jobdam_be.auth.exception.AuthErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;
    private final EmailProvider emailProvider;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final EmailVerificationDAO verificationDAO;
    private final UserDAO userDAO;
    private final RefreshDAO refreshDAO;

    public ResponseEntity<Map<String, Boolean>> checkEmail(String email) {
        Map<String, Boolean> response = new HashMap<>();
        boolean isDuplicate = userDAO.existsByEmail(email);

        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.ok().body(response);
    }

    @Transactional
    public ResponseEntity<String> signUp(SignUpDto dto) {
        String email = dto.getEmail();
        String code = UUID.randomUUID().toString();

        boolean isExist = userDAO.existsByEmail(email);
        if (isExist) {
            throw new AuthException(DUPLICATE_EMAIL);
        }
        String password = dto.getPassword();
        String encodedPassword = passwordEncoder.encode(password);
        dto.setPassword(encodedPassword);

        User user = new User(dto);
        boolean isSaved = userDAO.save(user);
        if (!isSaved) {
            throw new AuthException(DB_ERROR);
        }

        EmailVerification verification = new EmailVerification(email, code,
                null);
        verificationDAO.saveOrUpdateVerification(verification);

        CompletableFuture<Boolean> sent = emailProvider.sendVerificationMail(email, code);
        sent.thenAccept(isSuccessful -> {
            if (!isSuccessful) {
                throw new AuthException(MAIL_SEND_ERROR);
            }
        });

        return ResponseEntity.ok().body("메일 전송 성공");
    }

    @Transactional
    public boolean verifyEmail(String token) {
        EmailVerification verification = verificationDAO.findByToken(token).orElseThrow(() -> new AuthException(INVALID_TOKEN));
        long timeElapsed = System.currentTimeMillis() - verification.getCreatedAt().getTime();
        if (timeElapsed > TimeUnit.MINUTES.toMillis(5)) {
            throw new AuthException(EXPIRED_TOKEN);
        }

        Optional<User> findUser = userDAO.findByEmail(verification.getEmail());
        if (findUser.isEmpty()) {
            return false;
        }
        User user = findUser.get();
        if (user.getCreatedAt() != null) return true;

        verificationDAO.deleteByEmail(user.getEmail());
        userDAO.updateCreatedAtByEmail(user.getEmail());

        return true;
    }

    @Transactional
    public void resendVerificationEmail(ResendDto dto) {
        String email = dto.getEmail();
        User user = userDAO.findByEmail(email).orElseThrow(() -> new AuthException(INVALID_USER));

        if (user.getCreatedAt() != null) {
            throw new AuthException(DUPLICATE_EMAIL);
        }

        String code = UUID.randomUUID().toString();

        EmailVerification verification = new EmailVerification(user.getEmail(), code, null);
        verificationDAO.saveOrUpdateVerification(verification);

        CompletableFuture<Boolean> sent = emailProvider.sendVerificationMail(email, code);
        sent.thenAccept(isSuccessful -> {
            if (!isSuccessful) {
                throw new AuthException(MAIL_SEND_ERROR);
            }
        });
    }

//    @Transactional
//    public ResponseEntity<String> emailVerification(EmailVerificationDto dto) {
//        try {
//            // 기존에 존재하는 이메일인지
//            String email = dto.getEmail();
//            boolean isExistId = userDAO.existsByEmail(email);
//            if (isExistId) {
//                throw new AuthException(DUPLICATE_EMAIL);
//            }
//
//            String verificationCode = VerificationCode.getVerificationCode();
//            boolean sent = emailProvider.sendVerificationMail(email, verificationCode).get(5, TimeUnit.SECONDS);
//
//            if (!sent) {
//                throw new AuthException(MAIL_SEND_ERROR);
//            }
//
//            EmailVerification verification = new EmailVerification(
//                    email,
//                    verificationCode,
//                    new Timestamp(System.currentTimeMillis()));
//
//            verificationDAO.saveOrUpdateVerification(verification);
//            return ResponseEntity.ok().body("메일이 성공적으로 전송되었습니다.");
//        } catch (AuthException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new AuthException(DB_ERROR);
//        }
//    }

//    public ResponseEntity<String> checkVerification(CheckVerificationDto dto) {
//        try {
//            String email = dto.getEmail();
//            String code = dto.getCode();
//
//            validateEmailAndCode(email, code);
//
//        } catch (AuthException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new AuthException(DB_ERROR);
//        }
//
//        return ResponseEntity.ok().body("인증이 확인되었습니다.");
//    }

    //    public ResponseEntity<String> signUp(SignUpDto dto) {
//        String email = dto.getEmail();        String code = dto.getCode();
//        boolean isExistId = userDAO.existsByEmail(email);
//        if (isExistId) { throw new AuthException(DUPLICATE_EMAIL); }
//        validateEmailAndCode(email, code);
//        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
//        if (!userDAO.save( new User(dto))) { throw new AuthException(DB_ERROR); }
//        verificationDAO.deleteByEmail(email);
//        return ResponseEntity.ok().body("메일 전송 성공");
//    }


//    private void validateEmailAndCode(String email, String code) {
//        // 시간 제한 (예: 5분)
////        long timeElapsed = System.currentTimeMillis() - stored.getCreatedAt().getTime();
////        if (timeElapsed > TimeUnit.MINUTES.toMillis(5)) {
////            emailVerificationMapper.deleteByEmail(email);
////            throw new AuthException("인증 코드가 만료되었습니다.");
////        }
//
//        Optional<EmailVerification> findEmailVerify = verificationDAO.findByEmail(email);
//        if (findEmailVerify.isEmpty()) {
//            throw new AuthException(EMAIL_VERIFICATION_REQUIRED);
//        }
//        EmailVerification verification = findEmailVerify.get();
//        boolean isMatched = verification.getEmail().equals(email) && verification.getToken().equals(code);
//        if (!isMatched) {
//            throw new AuthException(INVALID_EMAIL_OR_PASSWORD);
//        }
//    }

    public ResponseEntity<String> reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) throws AuthException {
        String refresh = null;
        // 리프레시 토큰이 존재하는지
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("REFRESH_TOKEN")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            throw new AuthException(TOKEN_NOT_FOUND);
        }

        // 리프레시 토큰이 만료되었는지
        try {
            jwtProvider.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new AuthException(EXPIRED_TOKEN);
        } catch (io.jsonwebtoken.JwtException e) {
            throw new AuthException(INVALID_SIGNATURE);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtProvider.getCategory(refresh);
        if (!category.equals("REFRESH_TOKEN")) {
            throw new AuthException(INVALID_TOKEN);
        }

        // DB에 저장되어 있는지 확인
        boolean isExist = refreshDAO.existsByRefreshToken(refresh);
        if (!isExist) {
            throw new AuthException(INVALID_TOKEN);
        }

        // JWT 생성
        Long userId = jwtProvider.getUserId(refresh);
        String newAccess = jwtProvider.createJwt("ACCESS_TOKEN", userId, 600000L);
        String newRefresh = jwtProvider.createJwt("REFRESH_TOKEN", userId, 86400000L);

        // Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshDAO.deleteByRefreshToken(refresh);
        boolean isSaved = jwtService.saveRefreshToken(userId, refresh, 86400000L);
        if (!isSaved) {
            throw new AuthException(DB_ERROR);
        }

        //response
        response.setHeader("ACCESS_TOKEN", newAccess);
        response.addCookie(jwtService.createRefreshCookie(newRefresh));

        return ResponseEntity.ok().body("메일 전송 성공");
    }
}
