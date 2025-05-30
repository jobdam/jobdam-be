package com.jobdam.jobdam_be.videoChat.dto;

import com.jobdam.jobdam_be.videoChat.type.SignalType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class SdpSignalDTO {

    @Getter
    public static class Request {
        private SignalType signalType;
        private Long receiverId;
        private String sdp;
    }

    @Getter
    @Setter
    @Builder
    public static class Response{
        private SignalType signalType;
        private Long senderId;
        private Long receiverId;
        private String sdp;
    }
}
