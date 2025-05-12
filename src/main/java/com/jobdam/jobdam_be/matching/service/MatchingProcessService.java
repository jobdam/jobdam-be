package com.jobdam.jobdam_be.matching.service;

import com.jobdam.jobdam_be.chat.storage.ChatRoomStore;
import com.jobdam.jobdam_be.matching.controller.MatchingWsController;
import com.jobdam.jobdam_be.matching.model.MatchWaitingUserInfo;
import com.jobdam.jobdam_be.matching.pool.MatchingWaitingPool;
import com.jobdam.jobdam_be.matching.type.MatchType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingProcessService {

    private final MatchingWaitingPool matchingWaitingPool;
    private final ChatRoomStore chatRoomStore;//빈방 조회용
    private final MatchingWsController matchingWsController;

    private static final int GROUP_MAX = 6; //최대인원수

    //매칭 시작!
    public Optional<List<MatchWaitingUserInfo>> findMatch(MatchWaitingUserInfo target) {
        // 상태 마킹: 검사 중
        target.setInProgress(true);

        try {
            //그룹방일경우 먼저 채팅방에서 6명이하 방찾아서 넣음.
            if (target.getMatchType() == MatchType.GROUP) {//매칭타입이 그룹이면
                //꽉차지 않은 방 roomId를 반환해준다.
                boolean handled = chatRoomStore.findAvailableGroupRoom(GROUP_MAX)
                        .map(roomId -> {
                            chatRoomStore.add(roomId, MatchType.GROUP, target.getInterviewPreference());
                            matchingWsController.userEnterEmptyRoom(target, roomId);
                            return true;
                        })
                        .orElse(false);

                if (handled) return Optional.empty();
            }

            List<MatchWaitingUserInfo> candidates = new ArrayList<>();//매칭되면 넣을 list
            //1:1 group none 체크해서 none이면 1:1 group둘다 순회시킴
            List<MatchType> matchTypes = getSearchableTypes(target.getMatchType());

            for (MatchType type : matchTypes) {
                //매칭 풀에서 같은직군, 같은타입체크해서 리스트로 반환
                List<MatchWaitingUserInfo> poolList =
                        matchingWaitingPool.getReadyWaitingList(target.getJobGroupCode(), type);
                //매칭을 시작(스케쥴에서 선택된 유저랑 pool List랑 비교해서 필터로 골라줌)
                List<MatchWaitingUserInfo> matched = filterCandidates(target, poolList);
                candidates.addAll(matched);

                //1:1일경우 2명까지, max 5명(for문으로 도니까 체크해야함) 방이완성되면
                if (isMatchEnough(target.getMatchType(), candidates.size())) {
                    //인원수를 뽑아줌 1:1은 1명, 그룹은 5명이내로
                    List<MatchWaitingUserInfo> selected = selectMatch(target.getMatchType(), candidates);
                    String roomId = UUID.randomUUID().toString();

                    //매칭에 선택된 인원들 채팅방에추가
                    selected.forEach(user -> {
                        user.setInProgress(true);
                        chatRoomStore.add(roomId, user.getMatchType(), user.getInterviewPreference());
                    });
                    //매칭 루프 돌아가는 본인 채팅방에추가
                    chatRoomStore.add(roomId, target.getMatchType(), target.getInterviewPreference());

                    List<MatchWaitingUserInfo> fullList = new ArrayList<>(selected);
                    fullList.add(target);

                    matchingWsController.matchingComplete(fullList, roomId);

                    return Optional.of(selected);
                }
            }
            return Optional.empty();

        } finally {
            // 실패 시 inProgress 복구
            if (!Thread.currentThread().isInterrupted()) {
                target.setInProgress(false);
            }
        }
    }
    //None이면 1:1, group조회해서 매칭빈곳 아무데나 우선수위로 넣음
    private List<MatchType> getSearchableTypes(MatchType type) {
        if (type == MatchType.NONE) {
            return List.of(MatchType.ONE_TO_ONE, MatchType.GROUP);
        }
        return List.of(type);
    }
    //타켓이랑 같은직군, 같은매칭 비교
    private List<MatchWaitingUserInfo> filterCandidates(MatchWaitingUserInfo target, List<MatchWaitingUserInfo> poolList) {
        Instant now = Instant.now();

        return poolList.stream()
                .filter(candidate -> !candidate.getSessionId().equals(target.getSessionId())) // 자기 제외
                .filter(candidate -> {//두번째조건 3분이내 매칭인지(서브 조건확인)
                    boolean strict = target.getJoinedAt().plusSeconds(180).isAfter(now)
                            && candidate.getJoinedAt().plusSeconds(180).isAfter(now);
                    if (strict) {//3분이내 서브조건 검사(세부직군/신입,경력구분이같은지 체크)
                        return Objects.equals(candidate.getJobDetailCode(), target.getJobDetailCode()) &&
                                candidate.getExperienceType() == target.getExperienceType();
                    }//매칭 시간지나면 그냥 필터통과시켜줌
                    return true; // relaxed
                })
                .collect(Collectors.toList());
    }
    //매칭이 충분한지 체크
    private boolean isMatchEnough(MatchType type, int count) {
        if (type == MatchType.ONE_TO_ONE || type == MatchType.NONE) {
            return count >= 1;
        }
        return count >= 2; // target 제외 2명 = 3인 그룹
    }
    //매칭을 선택(1:1은 1명골라주고 그룹은 5명미만골라줌)
    private List<MatchWaitingUserInfo> selectMatch(MatchType type, List<MatchWaitingUserInfo> candidates) {
        if (type == MatchType.ONE_TO_ONE || type == MatchType.NONE) {
            return List.of(candidates.get(0));
        }
        return candidates.stream().limit(GROUP_MAX - 1).collect(Collectors.toList()); // target 제외 최대 5명 → 6명 방
    }

}
