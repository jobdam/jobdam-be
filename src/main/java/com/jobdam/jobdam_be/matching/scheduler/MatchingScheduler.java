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

//현재 소수인원테스트라 동기방식으로 사용
//인원수 늘어나고 1초이상걸리게될경우 비동시방식+중복매칭처리해야줘야함
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchingScheduler {
    private final MatchingWaitingPool matchingWaitingPool;
    private final MatchingProcessService matchingProcessService;
    private final MatchingWsController matchingWsController;

    @Scheduled(fixedRate = 1000) // 1초마다 실행
    public void runMatchProcess() {
        for (String jobGroup : matchingWaitingPool.getAll().keySet()) {
            for (MatchType type : MatchType.values()) {
                List<MatchWaitingUserInfo> waitingList = matchingWaitingPool.getWaitingList(jobGroup, type);
                for (MatchWaitingUserInfo user : waitingList) {
                    Optional<List<MatchWaitingUserInfo>> matched =
                            matchingProcessService.findMatch(user);

                    matched.ifPresent(matchList -> {
                        String roomId = UUID.randomUUID().toString();

                        // 방 참가자 전부 포함 (target + matched)
                        List<MatchWaitingUserInfo> fullList = new ArrayList<>(matchList);
                        fullList.add(user);

                        matchingWsController.matchingComplete(fullList,roomId);

                        log.info("매칭 성공! 직군={} 유형={} 인원={}", jobGroup, type, matchList.size() + 1);
                    });
                }
            }
        }
    }

}
