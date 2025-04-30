package com.jobdam.jobdam_be.websokect.dto.webRTCSignal;

import lombok.Getter;

@Getter
public class CandidateSignalDTO {
    private Long senderId;
    private Long receiverId;
    private String candidate;
    private String sdpMid;
    private Integer sdpMLineIndex;
}
