package com.jobdam.jobdam_be.websokect.sessionTracker.domain;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.jobdam.jobdam_be.chat.event.ChatSessionEvent;
import com.jobdam.jobdam_be.chat.type.ChatMessageType;
import com.jobdam.jobdam_be.interview.event.InterviewLeaveEvent;
import com.jobdam.jobdam_be.websokect.sessionTracker.WebSocketSessionTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component("signal")
@RequiredArgsConstructor
public class WebRTCSignalSessionTracker implements WebSocketSessionTracker {
    // 방번호/세션set
    private final Map<String, Set<String>> sessionMap = new ConcurrentHashMap<>();
    //세션아이디/userId 맵핑
    private final BiMap<String, Long> sessionIdToUserIdMap = Maps.synchronizedBiMap(HashBiMap.create());

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void addSession(String videoChatRoomId, String sessionId) {
        sessionMap.computeIfAbsent(videoChatRoomId,
                k-> ConcurrentHashMap.newKeySet()).add(sessionId);
    }
    public void addSessionUserMapping(String sessionId, Long userId){
        sessionIdToUserIdMap.put(sessionId,userId);
    }

    public Long getUserId(String sessionId){
        return sessionIdToUserIdMap.get(sessionId);
    }

    public String getSessionId(Long userId){
        return sessionIdToUserIdMap.inverse().get(userId);
    }

    //자신제외 방에있는 유저정보를 List로!
    public List<Long> getOtherUserIds(String roomId, String sessionId) {
        return sessionMap.getOrDefault(roomId, Collections.emptySet())//null이면 빈컬렉션으로
                .stream()
                .filter(sid -> !sid.equals(sessionId))
                .map(this::getUserId)
                .toList();
    }
    //방의 전체유저 조회
    public List<Long> getAllUserIds(String roomId) {
        return sessionMap.getOrDefault(roomId, Collections.emptySet())
                .stream()
                .map(this::getUserId)
                .filter(Objects::nonNull)
                .toList();
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
        if(!Objects.isNull(getUserId(sessionId))) {
            eventPublisher.publishEvent(new InterviewLeaveEvent(getUserId(sessionId)));
            sessionIdToUserIdMap.remove(sessionId);
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
        if(!Objects.isNull(getUserId(sessionId))) {
            eventPublisher.publishEvent(new InterviewLeaveEvent(getUserId(sessionId)));
            sessionIdToUserIdMap.remove(sessionId);
        }
    }
}
