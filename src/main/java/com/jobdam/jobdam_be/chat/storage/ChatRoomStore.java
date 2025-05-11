package com.jobdam.jobdam_be.chat.storage;

import com.jobdam.jobdam_be.chat.model.ChatRoom;
import com.jobdam.jobdam_be.matching.model.InterviewPreference;
import com.jobdam.jobdam_be.matching.type.MatchType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ChatRoomStore {
    //roomID / 채팅방(매칭타입,유저들)
    private final Map<String, ChatRoom> roomMap = new ConcurrentHashMap<>();

    //유저 한명 추가
    public void add(String roomId, MatchType matchType, InterviewPreference preference) {
        roomMap.computeIfAbsent(roomId, k -> new ChatRoom(matchType, new CopyOnWriteArrayList<>()))
                .getInterviewPreferenceList().add(preference);
    }

    //해당방의 참가자 목록조회
    public Optional<List<InterviewPreference>> get(String roomId) {
        return Optional.ofNullable(roomMap.get(roomId))
                .map(ChatRoom::getInterviewPreferenceList);
    }

    //특정 roomID의 matchType조회
    public Optional<MatchType> getMatchType(String roomId) {
        return Optional.ofNullable(roomMap.get(roomId))
                .map(ChatRoom::getMatchType);
    }

    //방을 제거
    public void remove(String roomId) {
        roomMap.remove(roomId);
    }
    //방의 현재 인원수
    public int getRoomSize(String roomId) {
        return roomMap.getOrDefault(roomId, new ChatRoom(MatchType.GROUP, List.of()))
                .getInterviewPreferenceList().size();
    }

    //방이 가득찼는지 확인
    public boolean isRoomFull(String roomId, int maxSize) {
        return getRoomSize(roomId) >= maxSize;
    }
    //최대인원수보다 작은방을 찾아준다.
    public Optional<String> findAvailableGroupRoom(int maxSize) {
        return roomMap.entrySet().stream()
                .filter(entry -> entry.getValue().getMatchType() == MatchType.GROUP) //그룹방필터
                .filter(entry -> entry.getValue().getInterviewPreferenceList().size() < maxSize)//최대인원수보다 적은방만
                .map(Map.Entry::getKey)//roomId만 반환
                .findFirst();
    }
}
