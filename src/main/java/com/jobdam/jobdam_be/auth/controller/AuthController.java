package com.jobdam.jobdam_be.auth.controller;

import com.jobdam.jobdam_be.auth.dto.CheckVerificationRequestDto;
import com.jobdam.jobdam_be.auth.dto.EmailVerificationRequestDto;
import com.jobdam.jobdam_be.auth.service.AuthService;
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
    public ResponseEntity<Map<String, Boolean>> checkEmail(@RequestBody String email) {
        return authService.checkEmail(email);
    }

    @PostMapping("/email-verification")
    public ResponseEntity<?> emailVerification(@RequestBody @Valid EmailVerificationRequestDto requestBody) {
        return authService.emailVerification(requestBody);
    }

    @PostMapping("/check-verification")
    public ResponseEntity<?> checkVerification(@RequestBody @Valid CheckVerificationRequestDto requestBody) {
        return authService.checkVerification(requestBody);
    }
}
