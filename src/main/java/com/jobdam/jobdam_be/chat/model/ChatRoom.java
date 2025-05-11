package com.jobdam.jobdam_be.chat.model;

import com.jobdam.jobdam_be.matching.model.InterviewPreference;
import com.jobdam.jobdam_be.matching.type.MatchType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChatRoom {
    private final MatchType matchType;
    private final List<InterviewPreference> interviewPreferenceList;
}
