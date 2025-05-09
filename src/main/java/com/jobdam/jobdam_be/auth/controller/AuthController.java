package com.jobdam.jobdam_be.auth.controller;

import com.jobdam.jobdam_be.auth.dto.ResendDTO;
import com.jobdam.jobdam_be.auth.dto.SignUpDTO;
import com.jobdam.jobdam_be.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        return authService.checkEmail(email);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody @Valid SignUpDTO requestBody) {
        return authService.signUp(requestBody);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestBody @Valid ResendDTO dto) {
        authService.resendVerificationEmail(dto);
        return ResponseEntity.ok("인증 메일이 다시 발송되었습니다.");
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, Boolean>> verify(@RequestParam String token) {
        Long userId = authService.verifyEmail(token);

        return authService.isProfileSetup(userId);
    }

    // Social 쿠키 검증 후 헤더에 토큰 제공
    @GetMapping("/oauth-redirect")
    public ResponseEntity<Map<String, Boolean>> oauthRedirect(@RequestParam String token, HttpServletResponse response) {
        Long userId = authService.setLoginToken(token, response);
        return authService.isProfileSetup(userId);
    }

    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        return authService.reissueRefreshToken(request, response);
    }

}
