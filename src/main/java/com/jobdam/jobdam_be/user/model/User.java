package com.jobdam.jobdam_be.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private long userId;
    private String name;
    private String password;
    private String role;
    private String userName;
}
