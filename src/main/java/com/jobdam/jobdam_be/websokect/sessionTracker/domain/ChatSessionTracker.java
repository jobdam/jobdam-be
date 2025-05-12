package com.jobdam.jobdam_be.websokect.sessionTracker.domain;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.jobdam.jobdam_be.chat.storage.ChatRoomStore;
import com.jobdam.jobdam_be.websokect.sessionTracker.WebSocketSessionTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component("chat")
@RequiredArgsConstructor
public class ChatSessionTracker implements WebSocketSessionTracker {

    private final Map<String, Set<String>> sessionMap = new ConcurrentHashMap<>();
    //세션아이디/userId 맵핑
    private final BiMap<String, Long> sessionIdToUserIdMap = Maps.synchronizedBiMap(HashBiMap.create());
    private final ChatRoomStore chatRoomStore;

    @Override
    public void addSession(String roomId, String sessionId) {
        sessionMap.computeIfAbsent(roomId,
                k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        //접송중
        Long userId = sessionIdToUserIdMap.get(sessionId);
        if (userId != null) {
            chatRoomStore.markConnected(roomId, userId);
        }
    }

    public void addSessionUserMapping(String sessionId, Long userId) {
        sessionIdToUserIdMap.put(sessionId, userId);
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
        Long userId = sessionIdToUserIdMap.remove(sessionId);
        if (userId != null) {
            chatRoomStore.markDisconnected(roomId, userId);
        }
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
                Long userId = sessionIdToUserIdMap.remove(sessionId);
                if (userId != null) {
                    chatRoomStore.markDisconnected(roomId, userId);
                    break;
                }
            }
        }
    }
}
