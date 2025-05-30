package com.jobdam.jobdam_be.chat.controller;

import com.jobdam.jobdam_be.chat.dto.ChatUserInfoDTO;
import com.jobdam.jobdam_be.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    //한명의유저정보만 조회
    @GetMapping("/userInfo/{roomId}/{userId}")
    public ResponseEntity<ChatUserInfoDTO.Response> getChatUserInfo(@PathVariable String roomId, @PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getChatUserInfo(roomId, userId));
    }

    //방에 있는 유저 전부 조회
    @GetMapping("/userInfos/{roomId}")
    public ResponseEntity<List<ChatUserInfoDTO.Response>> getChatUserInfoList(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getChatUserInfoList(roomId));
    }

    //방나가기!
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> removeUserFromRoom(Authentication authentication,
                                                   @PathVariable String roomId){
        Long userId = Long.valueOf(authentication.getName());
        chatService.removeUserFromRoom(userId,roomId);
        return ResponseEntity.ok().build();
    }
}