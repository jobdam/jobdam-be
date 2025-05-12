package com.jobdam.jobdam_be.chat.scheduler;

import com.jobdam.jobdam_be.chat.storage.ChatRoomStore;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ChatScheduler {

    private final ChatRoomStore chatRoomStore;
    //20초마다 방에있는사람이 나갔는지 체크(1분이상 자리비운사람)
    // 웹소켓은 새로고침,나갔는지 판단이 안서기때문에..
    @Scheduled(fixedRate = 20_000)
    public void cleanUpChatRoomParticipants() {
        Instant cutoff = Instant.now().minusSeconds(60);
        chatRoomStore.removeDisconnectedBefore(cutoff);
    }
}
