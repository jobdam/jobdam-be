package com.jobdam.jobdam_be.user.dto;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String email;
    private String name;
    private Timestamp birthday;
    private String targetCompanySize;
    private String profileImgUrl;
    private String jobCode;
    private String jobDetailCode;
    private String experienceType;
    private String educationLevel;
    private String educationStatus;
}
