package com.jobdam.jobdam_be.auth.controller;

import com.jobdam.jobdam_be.auth.dto.EmailCertificationRequestDto;
import com.jobdam.jobdam_be.auth.dto.EmailVerificationRequestDto;
import com.jobdam.jobdam_be.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/email-verification")
    public ResponseEntity<?> emailVerification(@RequestBody @Valid EmailVerificationRequestDto requestBody) {
        return authService.emailVerification(requestBody);
    }
}
