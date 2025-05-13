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

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private UserService userService;
    private JobService jobService;
    private ChatRoomStore chatRoomStore;
    private ModelMapper modelMapper;

    public ChatUserInfoDTO.Response getChatUserInfo(String roomId, Long userId) {
        User user = userService.getUserById(userId);
        InterviewPreference info = chatRoomStore.getUserInfo(roomId, userId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.INVALID_USER));
        JobGroupDetailJoinModel jobGroupDetailJoinModel = jobService.getJobGroupDetailJoinModel(info.getJobDetailCode());

        ChatUserInfoDTO.Response chatUserInfoDTO = modelMapper.map(user, ChatUserInfoDTO.Response.class);
        chatUserInfoDTO.setJobGroup(jobGroupDetailJoinModel.getJobGroup());
        chatUserInfoDTO.setJobDetail(jobGroupDetailJoinModel.getJobDetail());
        chatUserInfoDTO.setExperienceType(info.getExperienceType().name());
        chatUserInfoDTO.setIntroduce(info.getIntroduce());
        chatUserInfoDTO.setInterviewType(info.getInterviewType());

       return chatUserInfoDTO;
    }
}
