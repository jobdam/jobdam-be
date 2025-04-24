package com.jobdam.jobdam_be.websokect.sessionTracker.domain.model;

import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebRTCSignalSessionInfo{
    private String userId;
    private String peerId;
}
