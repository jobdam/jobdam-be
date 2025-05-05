package com.jobdam.jobdam_be.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String name;
    private Timestamp birthday;
    private String targetCompanySize;
    private String jobCode;
    private String jobDetailCode;
    private String experienceType;
    private String educationLevel;
    private String educationStatus;
}
