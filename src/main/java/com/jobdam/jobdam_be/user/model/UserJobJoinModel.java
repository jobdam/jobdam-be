package com.jobdam.jobdam_be.user.model;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
public class UserJobJoinModel {
    private long id;
    private String email;
    private String name;
    private Timestamp birthday;
    private String targetCompanySize;
    private String profileImgUrl;

    // 직무
    private String jobCode;
    private String jobGroup;
    private String jobDetailCode;
    private String jobDetail;

    // 경력
    private String experienceType;
    // 학력
    private String educationLevel;
    private String educationStatus;


}
