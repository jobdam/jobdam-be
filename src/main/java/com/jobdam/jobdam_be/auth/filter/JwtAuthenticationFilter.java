package com.jobdam.jobdam_be.auth.filter;

import com.jobdam.jobdam_be.auth.exception.AuthErrorCode;
import com.jobdam.jobdam_be.auth.exception.JwtAuthException;
import com.jobdam.jobdam_be.auth.service.CustomUserDetails;
import com.jobdam.jobdam_be.auth.provider.JwtProvider;
import com.jobdam.jobdam_be.user.dao.UserDAO;
import com.jobdam.jobdam_be.user.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDAO userDAO;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 access키에 담긴 토큰을 꺼냄
        String accessToken = parseBearerToken(request);
        // 토큰이 없는 경우(ex 로그인 전 요청)는 인증 없이 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try {
            if (jwtProvider.isExpired(accessToken)) {
                throw new JwtAuthException(AuthErrorCode.EXPIRED_TOKEN);
            }
            // 토큰이 access인지 확인 (발급시 페이로드에 명시)
            String category = jwtProvider.getCategory(accessToken);
            if (!category.equals("ACCESS_TOKEN")) {
                throw new JwtAuthException(AuthErrorCode.INVALID_TOKEN);
            }

            // HACK: role 값을 추가한다면 해당 코드에도 변경해야 함
            Long userId = jwtProvider.getUserId(accessToken);
            // String role = jwtProvider.getRole(accessToken);
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new JwtAuthException(AuthErrorCode.INVALID_USER);
            }

            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (ExpiredJwtException e) {
            request.setAttribute("exception", AuthErrorCode.EXPIRED_TOKEN);
        } catch (JwtAuthException e) {
            request.setAttribute("exception", e.getErrorCode());
        } catch (Exception e) {
            request.setAttribute("exception", new JwtAuthException(AuthErrorCode.INVALID_TOKEN));
        }
        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        boolean hasAuthorization = StringUtils.hasText(authorization);
        if (!hasAuthorization) return null;

        boolean isBearer = authorization.startsWith("Bearer ");
        if (!isBearer) return null;

        return authorization.substring(7);

    }
}
