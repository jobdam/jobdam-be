package com.jobdam.jobdam_be.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RefreshToken {
    private Long userId;

    private String refreshToken;

    private String expiration;
}
