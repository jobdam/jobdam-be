package com.jobdam.jobdam_be.user.controller;

import com.jobdam.jobdam_be.s3.service.S3Service;
import com.jobdam.jobdam_be.user.dto.UserProfileDTO;
import com.jobdam.jobdam_be.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveProfile(@Valid @RequestPart("data") UserProfileDTO dto,
                                         @RequestPart("image") MultipartFile image) {
        // 유저 아이디 jwt에서 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(authentication.getName());

        String imgUrl = s3Service.uploadImage(image, null, userId);

        userService.initProfile(userId, dto, imgUrl);

        return ResponseEntity.ok("");
    }

}
