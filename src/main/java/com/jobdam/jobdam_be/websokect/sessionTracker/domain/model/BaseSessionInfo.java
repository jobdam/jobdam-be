package com.jobdam.jobdam_be.websokect.sessionTracker.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BaseSessionInfo {
    private String purpose;
    private String roomId;
}
