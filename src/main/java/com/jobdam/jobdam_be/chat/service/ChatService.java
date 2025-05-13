package com.jobdam.jobdam_be.chat.service;

import com.jobdam.jobdam_be.chat.dto.ChatUserInfoDTO;
import com.jobdam.jobdam_be.chat.exception.ChatErrorCode;
import com.jobdam.jobdam_be.chat.exception.ChatException;
import com.jobdam.jobdam_be.chat.storage.ChatRoomStore;
import com.jobdam.jobdam_be.job.model.JobGroupDetailJoinModel;
import com.jobdam.jobdam_be.job.service.JobService;
import com.jobdam.jobdam_be.matching.model.InterviewPreference;
import com.jobdam.jobdam_be.user.model.User;
import com.jobdam.jobdam_be.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private UserService userService;
    private JobService jobService;
    private ChatRoomStore chatRoomStore;
    //채팅방 유저정보 한명만조회
    public ChatUserInfoDTO.Response getChatUserInfo(String roomId, Long userId) {
        User user = userService.getUserById(userId);
        InterviewPreference info = chatRoomStore.getUserInfo(roomId, userId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.INVALID_USER));
        JobGroupDetailJoinModel jobGroupDetailJoinModel = jobService.getJobGroupDetailJoinModel(info.getJobDetailCode());

        return ChatUserInfoDTO.Response.builder()
                .userId(user.getId())
                .name(user.getName())
                .targetCompanySize(user.getTargetCompanySize())
                .profileImgUrl(user.getProfileImgUrl())
                .educationLevel(user.getEducationLevel())
                .educationStatus(user.getEducationStatus())
                .jobGroup(jobGroupDetailJoinModel.getJobGroup())
                .jobDetail(jobGroupDetailJoinModel.getJobDetail())
                .experienceType(info.getExperienceType())
                .introduce(info.getIntroduce())
                .interviewType(info.getInterviewType())
                .build();
    }
    //챗팅방 유저정보 전체조회
    public List<ChatUserInfoDTO.Response> getChatUserInfoList(String roomId) {
        List<InterviewPreference> infos = chatRoomStore.get(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.INVALID_ROOM));

        return infos.stream()
                .map(info -> {
                    User user = userService.getUserById(info.getUserId());
                    JobGroupDetailJoinModel jobGroupDetailJoinModel = jobService.getJobGroupDetailJoinModel(info.getJobDetailCode());

                    return ChatUserInfoDTO.Response.builder()
                            .userId(user.getId())
                            .name(user.getName())
                            .targetCompanySize(user.getTargetCompanySize())
                            .profileImgUrl(user.getProfileImgUrl())
                            .educationLevel(user.getEducationLevel())
                            .educationStatus(user.getEducationStatus())
                            .jobGroup(jobGroupDetailJoinModel.getJobGroup())
                            .jobDetail(jobGroupDetailJoinModel.getJobDetail())
                            .experienceType(info.getExperienceType())
                            .introduce(info.getIntroduce())
                            .interviewType(info.getInterviewType())
                            .build();
                })
                .toList();
    }
}
