package com.jobdam.jobdam_be.websokect.sessionTracker.domain;

import com.jobdam.jobdam_be.websokect.sessionTracker.WebSocketSessionTracker;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component("signal")
public class WebRTCSignalSessionTracker implements WebSocketSessionTracker {
    // peerId 기준으로 세션 목록 관리
    private final Map<String, Set<String>> sessionMap = new ConcurrentHashMap<>();

    @Override
    public String getRoomKeyHeader(){
        return "videoChatRoomId"; // STOMP 헤더에서 가져올 키
    }

    @Override
    public void addSession(String videoChatRoomId, String sessionId) {
        sessionMap.computeIfAbsent(videoChatRoomId,
                k-> ConcurrentHashMap.newKeySet()).add(sessionId);
        sessionMap.get("1").forEach(System.out::println);
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
    }
}
