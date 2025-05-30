package com.jobdam.jobdam_be.chat.dto;

import com.jobdam.jobdam_be.matching.type.ExperienceType;
import com.jobdam.jobdam_be.interview.type.InterviewType;
import lombok.Builder;
import lombok.Data;

public class ChatUserInfoDTO {

    @Data
    @Builder
    public static class Response{
        private long userId;
        private String name;
        private String targetCompanySize;
        private String profileImgUrl;
        private String educationLevel;
        private String educationStatus;

        //위에는 유저db 정보 아래는 프론트에서 입력한정보
        private String jobCode;
        private String jobGroup;
        private String jobDetailCode;
        private String jobDetail;
        private ExperienceType experienceType;
        private String introduce;
        private InterviewType interviewType;

        //화상채팅 준비상태
        private boolean ready;
    }
}
