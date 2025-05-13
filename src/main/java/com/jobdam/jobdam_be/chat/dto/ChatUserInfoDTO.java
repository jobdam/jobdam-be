package com.jobdam.jobdam_be.chat.dto;

import com.jobdam.jobdam_be.matching.type.ExperienceType;
import com.jobdam.jobdam_be.matching.type.InterviewType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.sql.Timestamp;

public class ChatUserInfoDTO {

    @Data
    @Builder
    public static class Response{
        private long id;
        private String name;
        private String targetCompanySize;
        private String profileImgUrl;
        private String educationLevel;
        private String educationStatus;

        //위에는 유저db 정보 아래는 프론트에서 입력한정보

        private String jobGroup;
        private String jobDetail;
        private String experienceType;
        private String introduce;
        private InterviewType interviewType;
    }
}
