package com.jobdam.jobdam_be.auth.service;

import com.jobdam.jobdam_be.auth.dao.EmailVerificationDAO;
import com.jobdam.jobdam_be.auth.dao.RefreshDAO;
import com.jobdam.jobdam_be.auth.dao.TempTokenDAO;
import com.jobdam.jobdam_be.auth.dto.ResendDTO;
import com.jobdam.jobdam_be.auth.dto.SignUpDTO;
import com.jobdam.jobdam_be.auth.exception.AuthException;
import com.jobdam.jobdam_be.auth.model.EmailVerification;
import com.jobdam.jobdam_be.auth.provider.EmailProvider;
import com.jobdam.jobdam_be.auth.provider.JwtProvider;
import com.jobdam.jobdam_be.auth.config.TokenProperties;
import com.jobdam.jobdam_be.user.dao.UserDAO;
import com.jobdam.jobdam_be.user.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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
    private final TokenProperties tokenProperties;

    private final JwtProvider jwtProvider;
    private final EmailProvider emailProvider;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final EmailVerificationDAO verificationDAO;
    private final UserDAO userDAO;
    private final RefreshDAO refreshDAO;
    private final TempTokenDAO tempTokenDAO;
    private final EmailVerificationDAO emailVerificationDAO;

    public ResponseEntity<Map<String, Boolean>> checkEmail(String email) {
        Map<String, Boolean> response = new HashMap<>();
        boolean isDuplicate = userDAO.existsByEmail(email);

        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.ok().body(response);
    }

    @Transactional
    public ResponseEntity<String> signUp(SignUpDTO dto) {
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
        boolean isSaved = userDAO.saveEmail(user);
        if (!isSaved) {
            throw new AuthException(DB_ERROR);
        }

        EmailVerification verification = new EmailVerification(email, code, null);
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
    public Long verifyEmail(String token) {
        EmailVerification verification = verificationDAO.findByToken(token).orElseThrow(() -> new AuthException(INVALID_TOKEN));

        long timeElapsed = System.currentTimeMillis() - verification.getCreatedAt().getTime();
        if (timeElapsed > TimeUnit.MINUTES.toMillis(5)) {
            throw new AuthException(EXPIRED_TOKEN);
        }

        Optional<User> findUser = userDAO.findByEmail(verification.getEmail());
        if (findUser.isEmpty()) {
            throw new AuthException(INVALID_USER);
        }
        User user = findUser.get();
        if (user.getCreatedAt() != null) return null;

        verificationDAO.deleteByEmail(user.getEmail());
        userDAO.updateCreatedAtByEmail(user.getEmail());

        return user.getId();
    }

    @Transactional
    public void resendVerificationEmail(ResendDTO dto) {
        String token = dto.getToken();
        Optional<EmailVerification> optional = emailVerificationDAO.findByToken(token);
        if (optional.isEmpty()) {
            throw new AuthException(INVALID_TOKEN);
        }
        EmailVerification emailVerification = optional.get();
        String email = emailVerification.getEmail();
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

    @Transactional
    public ResponseEntity<String> reissueRefreshToken(HttpServletRequest request, HttpServletResponse response) throws AuthException {
        TokenProperties.TokenConfig accessConfig = tokenProperties.getAccessToken();
        TokenProperties.TokenConfig refreshConfig = tokenProperties.getRefreshToken();

        String refresh = null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(refreshConfig.getName())) {
                refresh = cookie.getValue();
            }
        }

        validateRefresh(refresh);

        // JWT 생성
        Long userId = jwtProvider.getUserId(refresh);

        String newAccess = jwtProvider.createJwt(accessConfig.getName(), userId, accessConfig.getExpiry());
        String newRefresh = jwtProvider.createJwt(refreshConfig.getName(), userId, refreshConfig.getExpiry());

        // Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshDAO.deleteByRefreshToken(refresh);
        boolean isSaved = jwtService.saveRefreshToken(userId, refresh, refreshConfig.getExpiry());
        if (!isSaved) {
            throw new AuthException(DB_ERROR);
        }

        //response
        response.setHeader("Authorization", "Bearer " + newAccess);
        response.addCookie(jwtService.createRefreshCookie(newRefresh));

        return ResponseEntity.ok().body("토큰 재발급 성공");
    }

    @Transactional
    public Long setLoginToken(String token, HttpServletResponse response) {
        Long userId = tempTokenDAO.findUserIdByToken(token);
        if (userId == null) {
            throw new AuthException(INVALID_TOKEN);
        }

        TokenProperties.TokenConfig accessConfig = tokenProperties.getAccessToken();
        TokenProperties.TokenConfig refreshConfig = tokenProperties.getRefreshToken();

        String access = jwtProvider.createJwt(accessConfig.getName(), userId, accessConfig.getExpiry());

        String refreshToken = jwtProvider.createJwt(refreshConfig.getName(), userId, refreshConfig.getExpiry());
        Cookie refreshCookie = jwtService.createRefreshCookie(refreshToken);
        response.addCookie(refreshCookie);

        response.setHeader("Authorization", "Bearer " + access);

        tempTokenDAO.deleteByToken(token);

        return userId;
    }

    public ResponseEntity<Map<String, Boolean>> isProfileSetup(Long userId) {

        boolean isSetup = userDAO.existsJobById(userId);

        return ResponseEntity.ok().body(Map.of("isSetup", isSetup));
    }

    private void validateRefresh(String token) {
        TokenProperties.TokenConfig refreshConfig = tokenProperties.getRefreshToken();

        validateToken(token);

        // 토큰이 access 인지 확인 (발급시 페이로드에 명시)
        String category = jwtProvider.getCategory(token);
        if (!category.equals(refreshConfig.getName())) {
            throw new AuthException(INVALID_TOKEN);
        }

        // DB에 저장되어 있는지 확인
        boolean isExist = refreshDAO.existsByRefreshToken(token);
        if (!isExist) {
            throw new AuthException(INVALID_TOKEN);
        }
    }

    private void validateToken(String token) throws AuthException {
        if (token == null) {
            throw new AuthException(TOKEN_NOT_FOUND);
        }

        // 토큰이 만료되었는지
        try {
            jwtProvider.isExpired(token);
        } catch (ExpiredJwtException e) {
            throw new AuthException(EXPIRED_TOKEN, e);
        } catch (JwtException e) {
            throw new AuthException(INVALID_SIGNATURE, e);
        }
    }
}
