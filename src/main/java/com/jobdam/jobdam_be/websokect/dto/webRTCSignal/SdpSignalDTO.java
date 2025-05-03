package com.jobdam.jobdam_be.websokect.dto.webRTCSignal;

import com.jobdam.jobdam_be.websokect.type.SignalType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SdpSignalDTO {
    private SignalType signalType;
    private Long senderId;
    private Long receiverId;
    private String sdp;
}
