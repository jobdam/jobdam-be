package com.jobdam.jobdam_be.websokect.sessionTracker;

public interface WebSocketSessionTracker {
    void addSession(String roomId, String sessionId);//여기서 key는 roomId
    void removeSession(String roomId, String sessionId);
    void removeSession(String sessionId);
}
