package com.jobdam.jobdam_be.matching.controller;

import com.jobdam.jobdam_be.chat.model.ChatRoom;
import com.jobdam.jobdam_be.chat.storage.ChatRoomStore;
import com.jobdam.jobdam_be.matching.dto.MatchingJoinDTO;
import com.jobdam.jobdam_be.matching.model.MatchWaitingUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MatchingWsController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatRoomStore chatRoomStore;

    //매칭 완료되면 그사람에게 참여 신호를줌
    public void matchingComplete(List<MatchWaitingUserInfo> fullList, String roomId) {
        fullList.forEach(participant ->
                joinChatRoom(participant.getUserId(), participant.getJobGroupCode(), roomId, true));
    }

    //매칭중인유저가 3~6명방일때 비어있는방 참여
    public void userEnterEmptyRoom(MatchWaitingUserInfo userInfo, String roomId) {
        joinChatRoom(userInfo.getUserId(), userInfo.getJobGroupCode(), roomId, false);
    }

    private void joinChatRoom(Long userId, String jopGroup, String roomId, boolean firstJoin) {
        Date created = findCreateAtByRoomId(roomId);
        simpMessagingTemplate.convertAndSendToUser(
                String.valueOf(userId),
                "/queue/matching/" + jopGroup,
                new MatchingJoinDTO.Response(roomId, created, firstJoin)
        );
    }

    private Date findCreateAtByRoomId(String roomId) {
        Optional<ChatRoom> room = chatRoomStore.getRoom(roomId);
        return room.map(chatroom -> chatroom.getCreated()).orElse(null);
    }
}
