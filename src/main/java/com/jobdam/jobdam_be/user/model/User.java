package com.jobdam.jobdam_be.user.model;

import com.jobdam.jobdam_be.auth.dto.SignUpDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private long id;
    private String email;
    private String password;
    private String name;
    private Timestamp birthday;
    private String targetCompanySize;
    private String isDeleted;
    private String backlist;
    private String profileImgUrl;
    private String jobDetailCode;
    private String jobCode;

    public User(SignUpDto dto) {
        this.email = dto.getEmail();
        this.password = dto.getPassword();
    }
}
