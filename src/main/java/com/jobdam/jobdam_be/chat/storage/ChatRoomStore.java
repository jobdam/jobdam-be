package com.jobdam.jobdam_be.chat.storage;

import com.jobdam.jobdam_be.chat.model.ChatParticipant;
import com.jobdam.jobdam_be.chat.model.ChatRoom;
import com.jobdam.jobdam_be.matching.model.InterviewPreference;
import com.jobdam.jobdam_be.matching.type.MatchType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class ChatRoomStore {
    //roomID / 채팅방(매칭타입,유저들)
    private final Map<String, ChatRoom> roomMap = new ConcurrentHashMap<>();

    //방생성 및 유저 추가
    public void add(String roomId, MatchType matchType, InterviewPreference preference) {
        //첫입장시 방을 생성한다.
        ChatRoom room = roomMap.compute(roomId, (key, existingRoom) -> (
         Objects.requireNonNullElseGet(existingRoom, () -> new ChatRoom(matchType, preference))
        ));
        //방에 이미있는 유저인지 파악하고 넣어준다. (중복입장방지)
        if (!isUserInRoom(roomId, preference.getUserId())) {
            room.getParticipants().add(new ChatParticipant(preference));
        }
    }

    //방에 유저가 있는지 확인한다.
    public boolean isUserInRoom(String roomId, Long userId) {
        ChatRoom room = roomMap.get(roomId);
        if (room == null) return false;

        return room.getParticipants().stream()
                .anyMatch(p -> p.getInfo().getUserId().equals(userId));
    }

    //방번호로 chatRoom을 가져온다.
    public Optional<ChatRoom> getRoom(String roomId) {
        return Optional.ofNullable(roomMap.get(roomId));
    }

    //방,유저아이디로 유저가 작성한 정보 조회
    public Optional<InterviewPreference> getUserInfo(String roomId, Long userId) {
        ChatRoom room = roomMap.get(roomId);
        //비어있으면 빈값 리턴
        if (room == null) return Optional.empty();

        return room.getParticipants().stream()
                .map(ChatParticipant::getInfo)
                .filter(info -> info.getUserId().equals(userId))
                .findFirst();
    }
    //화상채팅 준비버튼
    public void markReady(String roomId, Long userId, boolean ready) {
        ChatRoom room = roomMap.get(roomId);
        if (room != null) {
            room.getParticipants().stream()
                    .filter(p -> p.getInfo().getUserId().equals(userId))
                    .findFirst()
                    .ifPresent(p -> p.setReady(ready));
        }
    }
    //전체가 다 ready인지 체크하기
    public boolean isAllReady(String roomId) {
        ChatRoom room = roomMap.get(roomId);
        if (room == null) return false;

        return room.getParticipants().stream()
                .filter(ChatParticipant::isConnected) // 연결된 사람만 대상으로
                .allMatch(ChatParticipant::isReady);  // 모두 ready이면 true
    }


    //해당방의 참가자 목록조회 (info만)
    public Optional<List<InterviewPreference>> get(String roomId) {
        return Optional.ofNullable(roomMap.get(roomId))
                .map(room -> room.getParticipants().stream()
                        .filter(ChatParticipant::isConnected)
                        .map(ChatParticipant::getInfo)
                        .toList());
    }
    //해당방 참가자의 목록조회하는데 participant 전체 조회
    public Optional<List<ChatParticipant>> getParticipants(String roomId) {
        return Optional.ofNullable(roomMap.get(roomId))
                .map(room -> room.getParticipants().stream()
                        .filter(ChatParticipant::isConnected)
                        .toList());
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
        return Optional.ofNullable(roomMap.get(roomId))
                .map(room -> (int) room.getParticipants().stream()
                        .filter(ChatParticipant::isConnected)
                        .count())
                .orElse(0);
    }

    //방이 가득찼는지 확인
    public boolean isRoomFull(String roomId, int maxSize) {
        return getRoomSize(roomId) >= maxSize;
    }
    //최대인원수보다 작은방들을 찾아준다.
    public List<String> findAvailableGroupRoom(int maxSize) {
        return roomMap.entrySet().stream()
                .filter(entry -> entry.getValue().getMatchType() == MatchType.GROUP) //그룹방필터
                .filter(entry -> entry.getValue().getParticipants().stream()
                        .filter(ChatParticipant::isConnected)
                        .count() < maxSize) //최대인원수보다 적은방만
                .map(Map.Entry::getKey)//roomId만 반환
                .toList();
    }
    // 연결 끊김 처리 (disconnect)
    public void markDisconnected(String roomId, Long userId) {
        ChatRoom room = roomMap.get(roomId);
        if (room != null) {
            room.getParticipants().stream()
                    .filter(p -> p.getInfo().getUserId().equals(userId))
                    .findFirst()
                    .ifPresent(p -> p.setConnected(false));
        }
    }

    // 재접속 처리
    public void markConnected(String roomId, Long userId) {
        ChatRoom room = roomMap.get(roomId);
        if (room != null) {
            room.getParticipants().stream()
                    .filter(p -> p.getInfo().getUserId().equals(userId))
                    .findFirst()
                    .ifPresent(p -> p.setConnected(true));
        }
    }

    // 일정 시간 이상 끊긴 유저 제거 (스케줄러에서 호출)
    public void removeDisconnectedBefore(Instant cutoff) {
        roomMap.values().forEach(room ->
                room.getParticipants().removeIf(p ->
                        !p.isConnected() &&
                                p.getLastDisconnectedAt() != null &&
                                p.getLastDisconnectedAt().isBefore(cutoff))
        );
    }

}
