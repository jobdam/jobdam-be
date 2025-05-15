package com.jobdam.jobdam_be.chat.model;

import com.jobdam.jobdam_be.matching.model.InterviewPreference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;

//유저가 채팅방에서 새로고침,접속끊김등으로인해
//나갔는지, 진짜나갔는지 판단하기위해 연결상태를 확인하는모델
//스케쥴을 통해서 일정시간이 지나면 storage에서 삭제을 시켜준다.
@Getter
@RequiredArgsConstructor
public class ChatParticipant {
    private final InterviewPreference info;
    private volatile boolean connected = true;//연결상태확인
    @Setter
    private volatile boolean ready = false;//화상채팅 준비상태
    private Instant lastDisconnectedAt;

    public void setConnected(boolean connected) {
        this.connected = connected;
        if (!connected) {
            this.lastDisconnectedAt = Instant.now();
        }
    }

}
