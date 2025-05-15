package com.jobdam.jobdam_be.websokect.sessionTracker.domain;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.jobdam.jobdam_be.chat.controller.ChatWSMessageController;
import com.jobdam.jobdam_be.chat.event.ChatSessionEvent;
import com.jobdam.jobdam_be.chat.storage.ChatRoomStore;
import com.jobdam.jobdam_be.chat.type.ChatMessageType;
import com.jobdam.jobdam_be.websokect.sessionTracker.WebSocketSessionTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component("chat")
@RequiredArgsConstructor
public class ChatSessionTracker implements WebSocketSessionTracker {

    private final Map<String, Set<String>> sessionMap = new ConcurrentHashMap<>();
    //세션아이디/userId 맵핑
    private final BiMap<String, Long> sessionIdToUserIdMap = Maps.synchronizedBiMap(HashBiMap.create());
    //userId/useName 맵핑 => 이유 비정상종료시에는 세션만남아있고
    //principal, 세션속성등이 다 날아간다. 클라에게 ~~님이 나가셨습니다 등을 간편하게 보여주기위해 사용
    //db 다녀 오는것 보다 효율적
    private final BiMap<Long, String> userIdToUserNameMap = Maps.synchronizedBiMap(HashBiMap.create());

    private final ChatRoomStore chatRoomStore;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void addSession(String roomId, String sessionId) {
        sessionMap.computeIfAbsent(roomId,
                k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        //접송중
        Long userId = sessionIdToUserIdMap.get(sessionId);
        if (userId != null) {
            chatRoomStore.markConnected(roomId, userId);
        }
        eventPublisher.publishEvent(new ChatSessionEvent
                (ChatMessageType.JOIN,roomId,userId,getNameById(userId)));
    }

    public void addSessionUserMapping(String sessionId, Long userId) {
        sessionIdToUserIdMap.put(sessionId, userId);
    }

    public void addUserNameMapping(Long userId, String userName) {
        userIdToUserNameMap.put(userId, userName);
    }

    public String getNameById(Long userId){
       return Optional.ofNullable(userIdToUserNameMap.get(userId))
               .orElse("알 수 없음");
    }

    @Override
    public void removeSession(String roomId, String sessionId) {
        Set<String> sessions = sessionMap.get(roomId);
        if (sessions != null) {
            sessions.remove(sessionId);
            // 만약 세션이 다 빠져서 비었으면 방 자체를 제거
            if (sessions.isEmpty()) {
                sessionMap.remove(roomId);
            }
        }
        //잠시 접속종류
        removeBiMap(roomId,sessionId);
    }

    @Override
    public void removeSession(String sessionId) {
        for (Map.Entry<String, Set<String>> entry : sessionMap.entrySet()) {
            String roomId = entry.getKey();
            Set<String> sessions = entry.getValue();
            if (sessions.remove(sessionId)) {
                if (sessions.isEmpty()) {
                    sessionMap.remove(roomId);
                }
                removeBiMap(roomId,sessionId);
                break;
                }
            }
    }

    private void removeBiMap(String roomId, String sessionId){
        Long userId = sessionIdToUserIdMap.remove(sessionId);
        if (userId != null) {
            chatRoomStore.markDisconnected(roomId, userId);
         String userName = userIdToUserNameMap.remove(userId);
            eventPublisher.publishEvent(new ChatSessionEvent
                    (ChatMessageType.LEAVE,roomId,userId,userName));
        }
    }
}
