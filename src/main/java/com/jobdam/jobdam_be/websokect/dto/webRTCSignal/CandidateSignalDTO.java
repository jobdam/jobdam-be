package com.jobdam.jobdam_be.websokect.dto.webRTCSignal;

import com.jobdam.jobdam_be.websokect.type.SignalType;
import lombok.Getter;

@Getter
public class CandidateSignalDTO {
    private SignalType signalType;
    private Long senderId;
    private Long receiverId;
    private String candidate;
    private String sdpMid;
    private Integer sdpMLineIndex;
}
