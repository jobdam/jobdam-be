package com.jobdam.jobdam_be.websokect.sessionTracker;

public interface WebSocketSessionTracker {
    String getKeyHeader();
    void addSession(String key, String sessionId);
    void removeSession(String sessionId);
}
