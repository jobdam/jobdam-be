package com.jobdam.jobdam_be.websokect.sessionTracker;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component("signal")
public class SignalSessionTracker implements WebSocketSessionTracker{
    // peerId 기준으로 세션 목록 관리
    private final Map<String, Set<String>> sessionMap = new ConcurrentHashMap<>();

    @Override
    public String getKeyHeader() {
        return "peerId"; // STOMP 헤더에서 가져올 키
    }

    @Override
    public void addSession(String peerId, String sessionId) {
        //sessionMap.computeIfAbsent(peerId, k -> new HashSet<>()).add(sessionId);
    }

    @Override
    public synchronized void removeSession(String sessionId) {
    }
}
