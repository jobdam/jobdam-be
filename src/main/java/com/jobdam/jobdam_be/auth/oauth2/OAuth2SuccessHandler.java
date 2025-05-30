package com.jobdam.jobdam_be.auth.oauth2;

import com.jobdam.jobdam_be.auth.dao.TempTokenDAO;
import com.jobdam.jobdam_be.auth.model.OauthTempToken;
import com.jobdam.jobdam_be.auth.service.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TempTokenDAO tempTokenDAO;

    @Value("${frontEnd.url}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();

        Long id = oauthUser.getId();

        String uuid = UUID.randomUUID().toString();
        OauthTempToken token = new OauthTempToken(uuid, id);
        tempTokenDAO.save(token);
        String redirectUrl = frontUrl + "/oauth-redirect?token=" + uuid;
        response.sendRedirect(redirectUrl);
    }
}
