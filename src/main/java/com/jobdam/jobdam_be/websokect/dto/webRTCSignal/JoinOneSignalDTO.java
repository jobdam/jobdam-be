package com.jobdam.jobdam_be.websokect.dto.webRTCSignal;

import com.jobdam.jobdam_be.websokect.type.SignalType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinOneSignalDTO {
    private final SignalType signalType = SignalType.JOIN_ONE;
    private final Long userId;
}
