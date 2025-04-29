package com.jobdam.jobdam_be.auth.controller;

import com.jobdam.jobdam_be.auth.dto.CheckVerificationDto;
import com.jobdam.jobdam_be.auth.dto.EmailVerificationDto;
import com.jobdam.jobdam_be.auth.dto.SignUpDto;
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

    @PostMapping("/email-verification")
    public ResponseEntity<String> emailVerification(@RequestBody @Valid EmailVerificationDto requestBody) {
        return authService.emailVerification(requestBody);
    }

    @PostMapping("/check-verification")
    public ResponseEntity<String> checkVerification(@RequestBody @Valid CheckVerificationDto requestBody) {
        return authService.checkVerification(requestBody);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody @Valid SignUpDto requestBody) {
        return authService.signUp(requestBody);
    }

    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        return authService.reissueRefreshToken(request, response);
    }
}
