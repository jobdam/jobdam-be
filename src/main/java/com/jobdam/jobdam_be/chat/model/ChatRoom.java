package com.jobdam.jobdam_be.chat.model;

import com.jobdam.jobdam_be.matching.model.InterviewPreference;
import com.jobdam.jobdam_be.matching.type.MatchType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@RequiredArgsConstructor
public class ChatRoom {
    private final MatchType matchType;
    private final List<ChatParticipant> participants = new CopyOnWriteArrayList<>();
}
