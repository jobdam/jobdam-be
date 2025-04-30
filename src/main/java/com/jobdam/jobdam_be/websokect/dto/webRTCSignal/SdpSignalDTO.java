package com.jobdam.jobdam_be.websokect.dto.webRTCSignal;

import lombok.Getter;

@Getter
public class SdpSignalDTO {
    private Long senderId;
    private Long receiverId;
    private String sdp;
}
