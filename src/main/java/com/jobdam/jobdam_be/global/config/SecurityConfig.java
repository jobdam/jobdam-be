package com.jobdam.jobdam_be.global.config;

import com.jobdam.jobdam_be.auth.config.TokenProperties;
import com.jobdam.jobdam_be.auth.dao.RefreshDAO;
import com.jobdam.jobdam_be.auth.exception.CustomAccessDeniedHandler;
import com.jobdam.jobdam_be.auth.exception.CustomAuthenticationEntryPoint;
import com.jobdam.jobdam_be.auth.filter.CustomLogoutFilter;
import com.jobdam.jobdam_be.auth.filter.JwtAuthenticationFilter;
import com.jobdam.jobdam_be.auth.filter.LoginFilter;
import com.jobdam.jobdam_be.auth.oauth2.OAuth2SuccessHandler;
import com.jobdam.jobdam_be.auth.provider.JwtProvider;
import com.jobdam.jobdam_be.auth.service.JwtService;
import com.jobdam.jobdam_be.user.dao.UserDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenProperties tokenProperties;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final DefaultOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler customOAuth2SuccessHandler;

    private final JwtProvider jwtProvider;
    private final JwtService jwtService;

    private final RefreshDAO refreshDAO;
    private final UserDAO userDAO;

    @Value("${frontEnd.url}")
    private String frontendUrl;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //	JWT는 CSRF 공격 대상 아님 (쿠키 사용 안 함)
                .csrf(AbstractHttpConfigurer::disable)
                // 기본 로그인 폼 대신 직접 만든 필터 사용
                .formLogin(AbstractHttpConfigurer::disable)
                //	HTTP Basic 인증(username과 password를 헤더에 인코딩해서 보내는 방식) 대신 JWT 사용
                .httpBasic(AbstractHttpConfigurer::disable)
                // 세션 저장 없이 토큰만으로 인증 (stateless)
                .sessionManagement(sessionManagement -> sessionManagement.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                //oauth2
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfo) -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(customOAuth2SuccessHandler)
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)       // 인증 실패 시
                        .accessDeniedHandler(customAccessDeniedHandler)                 // 인가 실패 시
                )

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(
                                "/swagger-ui/**", "/check-email", "/login/**","/oauth2/**","/oauth-redirect", "/sign-up", "/resend-verification", "/verify", "verify-email-check", "/reissue",
                                "/termsAgreement", "/send", "/check-sns", "/debug/websocket/users"
                        ).permitAll()
                        .requestMatchers("/ws/**",
                                "/css/**", "/js/**", "/img/**", "/static/**", "/favicon.ico", "/WEB-INF/views/**"
                        ).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(tokenProperties, jwtProvider, userDAO), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new LoginFilter(tokenProperties, authenticationManager(authenticationConfiguration), jwtProvider, jwtService, refreshDAO, userDAO), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(tokenProperties, jwtProvider, refreshDAO), LogoutFilter.class)
        ;
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                frontendUrl
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
        configuration.setAllowCredentials(true);

        // 개발 중	0 또는 생략 (변경 반영 빠르게)
        // 배포 시 (안정된 정책)	600 ~ 3600 초
        // 보안이 매우 중요한 경우	60 초 이하로 낮게 유지
        configuration.setMaxAge(10L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
