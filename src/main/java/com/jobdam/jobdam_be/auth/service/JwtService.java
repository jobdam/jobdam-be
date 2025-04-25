package com.jobdam.jobdam_be.auth.service;

import com.jobdam.jobdam_be.auth.dao.RefreshDAO;
import com.jobdam.jobdam_be.auth.model.RefreshToken;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@RequiredArgsConstructor
public class JwtService {
    private final RefreshDAO refreshDAO;

    public boolean saveRefreshToken(Long userId, String refreshToken, long expiredMs) {
        Timestamp expiration = new Timestamp(System.currentTimeMillis() + expiredMs);
        RefreshToken entity = new RefreshToken(userId, refreshToken, expiration.toString());
        return refreshDAO.save(entity);
    }

    public Cookie createRefreshCookie(String token) {
        Cookie cookie = new Cookie("REFRESH_TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(24 * 60 * 60);
        return cookie;
    }
}
