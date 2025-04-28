package com.jobdam.jobdam_be.websokect.model.domain;

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
