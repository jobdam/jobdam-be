package com.jobdam.jobdam_be.user.controller;

import com.jobdam.jobdam_be.clova.service.ClovaAiService;
import com.jobdam.jobdam_be.s3.service.S3Service;
import com.jobdam.jobdam_be.user.dto.UserInitProfileDTO;
import com.jobdam.jobdam_be.user.dto.UserMatchingProfileDTO;
import com.jobdam.jobdam_be.user.dto.UserProfileDTO;
import com.jobdam.jobdam_be.user.model.Resume;
import com.jobdam.jobdam_be.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final S3Service s3Service;
    private final ClovaAiService clovaAiService;

    @PostMapping("/profile")
    public ResponseEntity<String> saveProfile(@Valid @RequestPart("data") UserInitProfileDTO dto,
                                              @RequestPart("image") MultipartFile image) {
        // 유저 아이디 jwt에서 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getName());

        String imgUrl = s3Service.uploadImage(image, null, userId);

        userService.initProfile(userId, dto, imgUrl);

        return ResponseEntity.ok("저장 성공");
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getName());

        return userService.getUserProfile(userId);
    }

    @PatchMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestPart("data") UserInitProfileDTO dto,
                                               @RequestPart(value = "image", required = false) MultipartFile image) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getName());

        String imgUrl = null;
        if (image != null && !image.isEmpty()) {
            String findImgUrl = userService.getUserProfileImage(userId);
            imgUrl = s3Service.uploadImage(image, findImgUrl, userId);
        }

        userService.updateProfile(userId, imgUrl, dto);

        return ResponseEntity.ok("업데이트 성공");
    }

    // Ai를 활용하여 pdf 정보 추출 및 질문 생성하기
    @PostMapping("/resume")
    public ResponseEntity<Map<String, String>> savePDF(@RequestParam MultipartFile pdfFile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getName());

        String resumeUrl = null;
        if (pdfFile != null && !pdfFile.isEmpty()) {
            String findImgUrl = userService.getUserResumeUrl(userId);

            resumeUrl = s3Service.uploadResume(pdfFile, findImgUrl, userId);
        }

        Resume resume = new Resume(null, userId, resumeUrl);
        userService.savePDF(resume);

        clovaAiService.analyzeResumeAndPDF(pdfFile, resume.getResumeId());

        // String pdfToString = PDFProvider.pdfToString(file);
        Map<String, String> response = new HashMap<>();
        response.put("resumeUrl", resumeUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/matching-profile")
    public ResponseEntity<UserMatchingProfileDTO.Response> getMyMatchingProfile(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(userService.getMyMatchingProfile(userId));
    }
}
