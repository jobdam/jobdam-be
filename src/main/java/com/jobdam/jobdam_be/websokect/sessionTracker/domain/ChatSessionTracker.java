package com.jobdam.jobdam_be.websokect.sessionTracker.domain;

import com.jobdam.jobdam_be.websokect.sessionTracker.WebSocketSessionTracker;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component("chat")
public class ChatSessionTracker implements WebSocketSessionTracker {

    private final Map<String, Set<String>> sessionMap = new ConcurrentHashMap<>();

    @Override
    public void addSession(String roomId, String sessionId) {
        sessionMap.computeIfAbsent(roomId,
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
