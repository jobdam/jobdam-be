package com.jobdam.jobdam_be.chat.model;

import com.jobdam.jobdam_be.matching.model.InterviewPreference;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;


@Getter
@RequiredArgsConstructor
public class ChatParticipant {
    private final InterviewPreference info;
    private volatile boolean connected = true;
    private Instant lastDisconnectedAt;

    public void setConnected(boolean connected) {
        this.connected = connected;
        if (!connected) {
            this.lastDisconnectedAt = Instant.now();
        }
    }
}
