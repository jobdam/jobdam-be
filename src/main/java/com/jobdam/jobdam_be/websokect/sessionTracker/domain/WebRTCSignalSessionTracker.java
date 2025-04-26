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
    }

    @Override
    public void removeSession(String sessionId) {
    }
}
