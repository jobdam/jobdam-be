package com.jobdam.jobdam_be.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification {
    private String email;
    private String code;
    private Timestamp createdAt;
}
