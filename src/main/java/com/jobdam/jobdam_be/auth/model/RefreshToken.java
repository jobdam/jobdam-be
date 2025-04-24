package com.jobdam.jobdam_be.auth.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshToken {
    private Long userId;

    private String refreshToken;

    private String expiration;
}
