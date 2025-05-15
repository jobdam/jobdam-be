package com.jobdam.jobdam_be.matching.service;

import com.jobdam.jobdam_be.chat.storage.ChatRoomStore;
import com.jobdam.jobdam_be.matching.controller.MatchingWsController;
import com.jobdam.jobdam_be.matching.model.InterviewPreference;
import com.jobdam.jobdam_be.matching.model.MatchWaitingUserInfo;
import com.jobdam.jobdam_be.matching.pool.MatchingWaitingPool;
import com.jobdam.jobdam_be.matching.type.MatchType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
/*
매칭 조건
1. 필수 조건 matchType (1:1, 3~6, none) // groupCode(직군)
2. 서브 조건 detailCode(세부직군),experienceType(신입,경력)
3. 서브조건은 3분이지나면 완화된다.
4. 3~6명방은 먼저 매칭되는인원 3~6명랜덤으로 입장하며
   이후에 추가로 group을 선택한 인원이 들어올 수 있다.
5. 추가로 오는 유저는 상세조건(조건완화전)이 완화된 채팅방인지 아닌지 체크를해서
     자신과 비교하고 들어가야한다.
6. 한번 나간곳은 기존방에 나갔는지 새로고침했는지 스케쥬을 통해서 체크를하는데
7. 진짜로 나갔는지는 1분정도를 기다려주는데 이 기간 동안에는 같은방 재입장이 불가능하다.
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingProcessService {

    private final MatchingWaitingPool matchingWaitingPool;
    private final ChatRoomStore chatRoomStore;//빈방 조회용
    private final MatchingWsController matchingWsController;

    private static final int GROUP_MAX = 6; //최대인원수

    //매칭 시작!
    public void findMatch(MatchWaitingUserInfo target) {
        // 상태 마킹: 검사 중 (중복으로 매칭방지)
        target.setInProgress(true);

        try {
            switch (target.getMatchType()) {
                case ONE_TO_ONE :
                    if(tryMatchOneToOne(target)) {
                        return;
                    }
                    break;

                case GROUP :
                    //먼저 비어있는방 체크!
                    if(tryEnterExistingGroupRoom(target)){
                        return;//함수종료
                    }
                    if(tryMatchGroup(target)){
                        return;
                    }
                    break;

                case NONE :
                    if(tryEnterExistingGroupRoom(target)){
                        return;//함수종료
                    }
                    if(tryMatchGroup(target)){
                        return;
                    }
                    if(tryMatchOneToOne(target)) {
                        return;
                    }
                    break;
            }
            target.setInProgress(false);
        } catch (Exception e) {
            log.error("[매칭 예외] userId={}, error={}", target.getUserId(), e.getMessage(), e);
        } finally {
            if (!Thread.currentThread().isInterrupted()) {
                target.setInProgress(false);
            }
        }
    }
    //1:1매칭 시도
    private boolean tryMatchOneToOne(MatchWaitingUserInfo target) {

        List<MatchWaitingUserInfo> poolList = new ArrayList<>();
        poolList.addAll(matchingWaitingPool.getReadyWaitingList(target.getJobGroupCode(), MatchType.ONE_TO_ONE));
        poolList.addAll(matchingWaitingPool.getReadyWaitingList(target.getJobGroupCode(), MatchType.NONE));

        List<MatchWaitingUserInfo> matched = filterCandidates(target, poolList);

        if (isMatchEnough(MatchType.ONE_TO_ONE, matched.size())) {//매칭인원이 충분하다면
            //만약초과인원이 있을 수 있으니 체크해준다
            List<MatchWaitingUserInfo> selected = selectMatch(target.getMatchType(), matched);
            makeRoom(target, selected, MatchType.ONE_TO_ONE);
            return true;
        }
        return false;
    }
    //그룹 매칭 시도
    private boolean tryMatchGroup(MatchWaitingUserInfo target) {

        List<MatchWaitingUserInfo> poolList = new ArrayList<>();
        poolList.addAll(matchingWaitingPool.getReadyWaitingList(target.getJobGroupCode(), MatchType.GROUP));
        poolList.addAll(matchingWaitingPool.getReadyWaitingList(target.getJobGroupCode(), MatchType.NONE));

        List<MatchWaitingUserInfo> matched = filterCandidates(target, poolList);

        if (isMatchEnough(MatchType.GROUP, matched.size())) {//매칭인원이 충분하다면
            //만약초과인원이 있을 수 있으니 체크해준다
            List<MatchWaitingUserInfo> selected = selectMatch(target.getMatchType(), matched);
            makeRoom(target, selected, MatchType.GROUP);
            return true;
        }
        return false;
    }

    //이미 있는 그룹방인 경우를 체크해서 들어가기
    private boolean tryEnterExistingGroupRoom(MatchWaitingUserInfo target){
        Optional<String> matchedRoomId = findCompatibleGroupRoom(target);
        if (matchedRoomId.isPresent()) {//비어있는방이 있다면!
            chatRoomStore.add(matchedRoomId.get(), MatchType.GROUP, target.getInterviewPreference());
            matchingWsController.userEnterEmptyRoom(target, matchedRoomId.get());
            return true;
        }
        return false;
    }

    //매칭 조건이 group의 경우 우선 빈방을 찾아서 매칭해주는 메소드이다.
    //아래 filterCandidates는 서로 매칭일경우 검증해주는 메소드이기떄문에
    //이미 있는방에 들어갈떄는 그방이 완화조건인지 내가 완화조건인지 비교해서 들어가야한다.
    private Optional<String> findCompatibleGroupRoom(MatchWaitingUserInfo target) {
        Instant now = Instant.now();
        boolean strict = target.getJoinedAt().plusSeconds(180).isAfter(now);
        //6명이하인 빈방을 찾아서 filter
        return chatRoomStore.findAvailableGroupRoom(GROUP_MAX).stream()
                .filter(roomId -> chatRoomStore.getRoom(roomId).map(room -> {
                    // 이미 이 방에 내가 들어가 있었으면 스킵
                    if (chatRoomStore.isUserInRoom(roomId, target.getUserId())) {
                        return false;
                    }
                    // 방 인원이 2명 이상 있어야 입장 대상
                    if (room.getParticipants().size() < 2) {
                        return false;
                    }
                    // 필수 조건: 직군코드
                    if (!Objects.equals(room.getJobGroupCode(), target.getJobGroupCode())) {
                        return false;
                    }
                    // strict일 경우 서브조건도 비교
                    if (strict) {
                        return Objects.equals(room.getJobDetailCode(), target.getJobDetailCode()) &&
                                room.getExperienceType() == target.getExperienceType();
                    }
                    // relaxed: 필수조건만 맞으면 입장 가능
                    return true;
                 }).orElse(false))
                .findFirst();//그중 첫번째방을 선택해 들어간다.
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
    private boolean isMatchEnough(MatchType matchType, int count) {
        if (matchType == MatchType.GROUP) {
            return count >= 2; // 그룹: target 제외 2명 이상 필요
        }
        return count >= 1; // 1:1: target 제외 1명 이상 필요
    }


    //매칭을 선택(1:1은 1명골라주고 그룹은 5명미만골라줌)
    private List<MatchWaitingUserInfo> selectMatch(MatchType type, List<MatchWaitingUserInfo> candidates) {
        if (type == MatchType.ONE_TO_ONE) {
            return List.of(candidates.get(0));
        }
        return candidates.stream().limit(GROUP_MAX - 1).collect(Collectors.toList());
    }

    //방만들고 유저보내기
    private void makeRoom(MatchWaitingUserInfo target, List<MatchWaitingUserInfo> selected, MatchType roomType) {
        String roomId = UUID.randomUUID().toString();

        //매칭에 선택된 인원들 채팅방에추가
        selected.forEach(user -> {
            user.setInProgress(true);
            chatRoomStore.add(roomId, roomType, user.getInterviewPreference());
        });
        //매칭 루프 돌아가는 본인 채팅방에추가
        chatRoomStore.add(roomId, roomType, target.getInterviewPreference());

        List<MatchWaitingUserInfo> fullList = new ArrayList<>(selected);
        fullList.add(target);
        matchingWsController.matchingComplete(fullList, roomId);

        log.info("[매칭 성공] roomId={}, type={}, users={}", roomId, roomType, fullList.stream().map(MatchWaitingUserInfo::getUserId).toList());
    }

}
