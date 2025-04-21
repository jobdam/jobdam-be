package com.jobdam.jobdam_be.user.controller;

import com.jobdam.jobdam_be.user.dto.UserDTO;
import com.jobdam.jobdam_be.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/test")
    public String test(@RequestBody UserDTO userDTO){
        userService.test(userDTO);
        return "test";
    }
}
