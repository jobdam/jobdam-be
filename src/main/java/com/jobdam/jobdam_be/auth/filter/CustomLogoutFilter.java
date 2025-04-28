package com.jobdam.jobdam_be.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobdam.jobdam_be.auth.dao.RefreshDAO;
import com.jobdam.jobdam_be.auth.exception.AuthErrorCode;
import com.jobdam.jobdam_be.auth.provider.JwtProvider;
import com.jobdam.jobdam_be.global.exception.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilter {

    private final JwtProvider jwtProvider;
    private final RefreshDAO refreshDAO;
    private static final String LOGOUT_URI = "/logout";
    private static final String REFRESH_COOKIE_NAME = "REFRESH_TOKEN";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!isLogoutRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        handleLogout(request, response);
    }

    /**
     * 현재 요청이 로그아웃 요청인지 여부를 확인하는 메서드
     */
    private boolean isLogoutRequest(HttpServletRequest request) {
        return LOGOUT_URI.equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod());
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String refreshToken = extractRefreshTokenFromCookies(request);
        try {
            if (refreshToken == null || !isValidRefreshToken(refreshToken)) {
                sendErrorResponse(response, AuthErrorCode.TOKEN_NOT_FOUND);
                return;
            }
        } catch (MalformedJwtException e) {
            sendErrorResponse(response, AuthErrorCode.INVALID_TOKEN);
            return;
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, AuthErrorCode.EXPIRED_TOKEN);
            return;
        }

        // 로그아웃 처리
        refreshDAO.deleteByRefreshToken(refreshToken);
        removeRefreshCookie(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Cookie 중 refresh 이름의 토큰을 찾아 반환
     */
    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> REFRESH_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * 주어진 refresh 토큰이 정상적인지 확인
     */
    private boolean isValidRefreshToken(String refresh) {
        jwtProvider.isExpired(refresh);

        return REFRESH_COOKIE_NAME.equals(jwtProvider.getCategory(refresh)) &&
                refreshDAO.existsByRefreshToken(refresh);
    }

    private void removeRefreshCookie(HttpServletResponse response) {
        Cookie expiredCookie = new Cookie(REFRESH_COOKIE_NAME, null);
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        response.addCookie(expiredCookie);
    }

    // 응답을 처리하는 메서드
    private void sendErrorResponse(HttpServletResponse response, AuthErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getCode());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage()).build();


        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
