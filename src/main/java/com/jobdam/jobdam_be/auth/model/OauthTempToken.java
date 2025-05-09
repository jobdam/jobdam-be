package com.jobdam.jobdam_be.auth.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OauthTempToken {
    private String tempToken;

    private Long userId;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime expiresAt = createdAt.plusMinutes(1);

    public OauthTempToken(String uuid, Long id) {
        tempToken = uuid;
        userId = id;
    }
}
