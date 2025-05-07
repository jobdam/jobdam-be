package com.jobdam.jobdam_be.auth.oauth2;

import com.jobdam.jobdam_be.auth.provider.JwtProvider;
import com.jobdam.jobdam_be.auth.service.CustomOAuth2User;
import com.jobdam.jobdam_be.auth.service.JwtService;
import com.jobdam.jobdam_be.auth.config.TokenProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProperties tokenProperties;
    private final JwtService jwtService;
    private final JwtProvider jwtProvider;

    @Value("${frontEnd.url}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();

        Long id = oauthUser.getId();

        TokenProperties.TokenConfig accessConfig = tokenProperties.getAccessToken();
        TokenProperties.TokenConfig refreshConfig = tokenProperties.getRefreshToken();

        String accessToken = jwtProvider.createJwt(accessConfig.getName(), id, accessConfig.getExpiry());
        String refreshToken = jwtProvider.createJwt(refreshConfig.getName(), id, refreshConfig.getExpiry());

        Cookie accessCookie = jwtService.createAccessCookie(accessToken);
        Cookie refreshCookie = jwtService.createRefreshCookie(refreshToken);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        response.sendRedirect(frontUrl + "/oauth-redirect");
    }
}
