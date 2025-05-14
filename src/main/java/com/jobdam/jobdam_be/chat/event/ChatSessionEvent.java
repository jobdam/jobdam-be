package com.jobdam.jobdam_be.chat.event;

import com.jobdam.jobdam_be.chat.type.ChatMessageType;

public record ChatSessionEvent (
    ChatMessageType chatMessageType,
    String roomId,
    Long userId,
    String userName
){}

