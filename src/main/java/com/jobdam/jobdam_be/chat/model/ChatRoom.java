package com.jobdam.jobdam_be.chat.model;

import com.jobdam.jobdam_be.matching.model.InterviewPreference;
import com.jobdam.jobdam_be.matching.type.ExperienceType;
import com.jobdam.jobdam_be.matching.type.MatchType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
//방이 매칭 완화된방인지, 아닌지 또한
//어떠한조건으로 들어온방인지 확인한다.
// jobDeatalCode가 null일경우에는 완화된 방으로 필수조건으로만 매칭된 방이다.
@Getter
@RequiredArgsConstructor
public class ChatRoom {
    private final MatchType matchType; // 필수
    private final String jobGroupCode; // 필수
    private final String jobDetailCode; // 선택
    private final ExperienceType experienceType; // 선택
    private final List<ChatParticipant> participants = new CopyOnWriteArrayList<>();

    public ChatRoom(MatchType matchType, InterviewPreference preference) {
        this.matchType = matchType;
        this.jobGroupCode = preference.getJobGroupCode();
        this.jobDetailCode = preference.getJobDetailCode();
        this.experienceType = preference.getExperienceType();
    }
}
