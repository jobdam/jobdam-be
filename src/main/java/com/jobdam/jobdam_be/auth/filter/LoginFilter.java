package com.jobdam.jobdam_be.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobdam.jobdam_be.auth.dao.RefreshDAO;
import com.jobdam.jobdam_be.auth.model.RefreshToken;
import com.jobdam.jobdam_be.auth.provider.JwtProvider;
import com.jobdam.jobdam_be.auth.service.JwtService;
import com.jobdam.jobdam_be.user.dao.UserDAO;
import com.jobdam.jobdam_be.user.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final JwtService jwtService;

    private final RefreshDAO refreshDAO;
    private final UserDAO userDAO;

    /**
     * POST /login 클라이언트가 로그인 POST 요청을 보낼 때 실행
     *
     * @return AuthenticationManager 내부에서  UserDetailsService를 통해 검사 -> 결과에 따라 성공, 실패 메서드 호출
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // JSON 요청 바디 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> loginData = objectMapper.readValue(request.getInputStream(), Map.class);

            String email = loginData.get("email");
            String password = loginData.get("password");

            if (email == null || password == null) {
                throw new AuthenticationServiceException("이메일 또는 비밀번호가 누락되었습니다.");
            }

            //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);

            //token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new AuthenticationServiceException("로그인 요청 파싱 실패", e);
        }
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        //유저 정보
        String email = authentication.getName();
        User user = userDAO.findByEmail(email);
        // HACK: role 값 사용시 사용
        // Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        // Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        // GrantedAuthority auth = iterator.next();
        // String role = auth.getAuthority();

        // 기존 refresh 토큰 삭제
        refreshDAO.deleteByUserId(user.getId());

        //토큰 생성
        String access = jwtProvider.createJwt("access", email, 600000L);        // 10분
        String refresh = jwtProvider.createJwt("refresh", email, 86400000L);    // 1일

        response.setHeader("access", access);
        Cookie refreshCookie = jwtService.createRefreshCookie(refresh);
        response.addCookie(refreshCookie);

        jwtService.saveRefreshToken(user.getId(), refresh, 86400000L);

        response.setStatus(HttpServletResponse.SC_OK);
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }


}
