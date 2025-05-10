package com.jobdam.jobdam_be.websokect.sessionTracker.domain;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.jobdam.jobdam_be.matching.pool.MatchingWaitingPool;
import com.jobdam.jobdam_be.websokect.sessionTracker.WebSocketSessionTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component("matching")
@RequiredArgsConstructor
public class MatchingSessionTracker implements WebSocketSessionTracker {
    // 방번호/세션set
    private final Map<String, Set<String>> sessionMap = new ConcurrentHashMap<>();

    private final MatchingWaitingPool matchingWaitingPool;
    @Override
    public void addSession(String videoChatRoomId, String sessionId) {
        sessionMap.computeIfAbsent(videoChatRoomId,
                k -> ConcurrentHashMap.newKeySet()).add(sessionId);
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
        matchingWaitingPool.removeByJobGroupAndSessionId(roomId, sessionId);
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
                break;
            }
        }
        matchingWaitingPool.removeBySessionId(sessionId);
    }
}
