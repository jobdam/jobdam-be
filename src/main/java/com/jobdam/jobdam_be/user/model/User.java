package com.jobdam.jobdam_be.user.model;

import com.jobdam.jobdam_be.auth.dto.SignUpDTO;
import com.jobdam.jobdam_be.user.type.EducationLevel;
import com.jobdam.jobdam_be.user.type.EducationStatus;
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

    // 직무
    private String jobCode;
    private String jobDetailCode;

    // 경력
    private String experienceType;
    // 학력
    private EducationLevel educationLevel;
    private EducationStatus educationStatus;

    private String providerId;

    public User(SignUpDTO dto) {
        this.email = dto.getEmail();
        this.password = dto.getPassword();
    }
}
