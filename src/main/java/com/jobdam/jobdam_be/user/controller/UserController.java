package com.jobdam.jobdam_be.user.controller;

import com.jobdam.jobdam_be.s3.service.S3Service;
import com.jobdam.jobdam_be.user.dto.UserInitProfileDTO;
import com.jobdam.jobdam_be.user.dto.UserProfileDTO;
import com.jobdam.jobdam_be.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final S3Service s3Service;

    @GetMapping("/test")
    public String test() {
        // userService.test();
        return "test";
    }

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

}
