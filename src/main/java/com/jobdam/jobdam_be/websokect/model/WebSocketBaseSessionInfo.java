package com.jobdam.jobdam_be.websokect.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WebSocketBaseSessionInfo {
    private String purpose;
    private String roomId;
}
