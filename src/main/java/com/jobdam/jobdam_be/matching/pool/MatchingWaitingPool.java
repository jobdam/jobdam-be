package com.jobdam.jobdam_be.matching.pool;

import com.jobdam.jobdam_be.matching.model.MatchWaitingUserInfo;
import com.jobdam.jobdam_be.matching.type.MatchType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Component
public class MatchingWaitingPool {
    // 구조: 직군 → 매칭유형 → 대기열 (ConcurrentQueue 기반)
    private final Map<String, Map<MatchType, ConcurrentLinkedQueue<MatchWaitingUserInfo>>> pool = new ConcurrentHashMap<>();

    // 유저 추가
    public void add(MatchWaitingUserInfo userInfo) {
        pool.computeIfAbsent(userInfo.getJobGroupCode(), k -> new ConcurrentHashMap<>())
                .computeIfAbsent(userInfo.getMatchType(), k -> new ConcurrentLinkedQueue<>())
                .add(userInfo);
    }

    // 대기 리스트 조회(완전매칭 초기상태)
    public List<MatchWaitingUserInfo> getWaitingList(String jobGroup, MatchType matchType) {
        return List.copyOf(pool.getOrDefault(jobGroup, Map.of())
                .getOrDefault(matchType, new ConcurrentLinkedQueue<>()));
    }
    //방들어가기전에 list에 담겨있는상태 조회
    public List<MatchWaitingUserInfo> getReadyWaitingList(String jobGroup, MatchType matchType) {
        return pool.getOrDefault(jobGroup, Map.of())
                .getOrDefault(matchType, new ConcurrentLinkedQueue<>())
                .stream()
                .filter(user -> !user.isInProgress())
                .collect(Collectors.toList());
    }
    
    // 세션 ID 기준 제거
    public void removeBySessionId(String sessionId) {
        pool.values().forEach(matchTypeMap ->
                matchTypeMap.values().forEach(queue ->
                        queue.removeIf(user -> user.getSessionId().equals(sessionId))
                )
        );
    }

    public void removeByJobGroupAndSessionId(String jobGroup, String sessionId) {
        Map<MatchType, ConcurrentLinkedQueue<MatchWaitingUserInfo>> typeMap = pool.get(jobGroup);
        if (typeMap == null) return;
        typeMap.values().forEach(queue ->
                queue.removeIf(user -> user.getSessionId().equals(sessionId))
        );
    }

    // 전체 조회 (디버깅 용도)
    public Map<String, Map<MatchType, ConcurrentLinkedQueue<MatchWaitingUserInfo>>> getAll() {
        return pool;
    }
}
