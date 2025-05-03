package com.jobdam.jobdam_be.user.model;

import com.jobdam.jobdam_be.auth.dto.SignUpDto;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
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
    private String blacklist;
    private String profileImgUrl;
    private Timestamp createdAt;
    private String jobDetailCode;
    private String jobCode;
    private String providerId;

    public User(SignUpDto dto) {
        this.email = dto.getEmail();
        this.password = dto.getPassword();
    }
}
