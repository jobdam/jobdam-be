package com.jobdam.jobdam_be.matching.scheduler;

import com.jobdam.jobdam_be.matching.controller.MatchingWsController;
import com.jobdam.jobdam_be.matching.model.MatchWaitingUserInfo;
import com.jobdam.jobdam_be.matching.pool.MatchingWaitingPool;
import com.jobdam.jobdam_be.matching.service.MatchingProcessService;
import com.jobdam.jobdam_be.matching.type.MatchType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

//현재 소수인원테스트라 동기방식으로 사용
//인원수 늘어나고 1초이상걸리게될경우 비동시방식+중복매칭처리해야줘야함
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingScheduler {
    private final MatchingWaitingPool matchingWaitingPool;
    private final MatchingProcessService matchingProcessService;

    @Scheduled(fixedRate = 1000) // 1초마다 실행
    public void runMatchProcess() {
        List<MatchWaitingUserInfo> waitingList = matchingWaitingPool.getAll().values().stream()
                .flatMap(typeMap -> typeMap.values().stream())
                .flatMap(ConcurrentLinkedQueue::stream)
                .filter(user -> !user.isInProgress())
                .toList();

        for (MatchWaitingUserInfo user : waitingList) {
            if (!user.isInProgress()) {
                // 매칭 시도
                try {
                    matchingProcessService.findMatch(user);
                } catch (Exception e) {
                    log.error("매칭 중 예외 발생 - user: {}", user.getSessionId(), e);
                    user.setInProgress(false); // 예외 시 상태 복구
                }
            }
        }

    }
}
