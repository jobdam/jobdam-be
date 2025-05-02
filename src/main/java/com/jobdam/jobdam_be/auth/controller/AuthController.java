package com.jobdam.jobdam_be.auth.controller;

import com.jobdam.jobdam_be.auth.dto.ResendDto;
import com.jobdam.jobdam_be.auth.dto.SignUpDto;
import com.jobdam.jobdam_be.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public ResponseEntity<String> signUp(@RequestBody @Valid SignUpDto requestBody) {
        return authService.signUp(requestBody);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestBody @Valid ResendDto dto) {
        authService.resendVerificationEmail(dto);
        return ResponseEntity.ok("인증 메일이 다시 발송되었습니다.");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token, HttpServletResponse response) throws IOException {
        authService.verifyEmail(token);

        response.sendRedirect("http://localhost:5173/verify-email-check");
        return ResponseEntity.ok("인증이 완료되었습니다!");
    }

    @PostMapping("/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        return authService.reissueRefreshToken(request, response);
    }
//    @PostMapping("/email-verification")
//    public ResponseEntity<String> emailVerification(@RequestBody @Valid EmailVerificationDto requestBody) {
//        return authService.emailVerification(requestBody);
//    }

//    @PostMapping("/check-verification")
//    public ResponseEntity<String> checkVerification(@RequestBody @Valid CheckVerificationDto requestBody) {
//        return authService.checkVerification(requestBody);
//    }
}
