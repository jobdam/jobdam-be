package com.jobdam.jobdam_be.videoChat.dto;

import com.jobdam.jobdam_be.videoChat.type.SignalType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class JoinListSignalDTO {
    private final SignalType signalType = SignalType.JOIN_LIST;
    private final List<Long> userIdList;
}
