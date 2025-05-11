package com.jobdam.jobdam_be.chat.storage;

import com.jobdam.jobdam_be.matching.model.InterviewPreference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ChatRoomStore {
    private final Map<String, List<InterviewPreference>> roomMap = new ConcurrentHashMap<>();

    public void add(String roomId, InterviewPreference preference) {
        roomMap.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>())
                .add(preference);
    }

    public Optional<List<InterviewPreference>> get(String roomId) {
        return Optional.ofNullable(roomMap.get(roomId));
    }

    public void remove(String roomId) {
        roomMap.remove(roomId);
    }

}
