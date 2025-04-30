package com.jobdam.jobdam_be.user.controller;

import com.jobdam.jobdam_be.user.dto.UserDTO;
import com.jobdam.jobdam_be.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    @GetMapping("/test")
    public String test(){
        // userService.test();
        return "test";
    }
}
