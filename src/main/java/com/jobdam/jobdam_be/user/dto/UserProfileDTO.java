package com.jobdam.jobdam_be.user.dto;

import com.jobdam.jobdam_be.user.type.EducationLevel;
import com.jobdam.jobdam_be.user.type.EducationStatus;
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
    private EducationLevel educationLevel;
    private EducationStatus educationStatus;
}
