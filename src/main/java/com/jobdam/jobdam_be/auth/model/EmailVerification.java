package com.jobdam.jobdam_be.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerification {
    private String email;
    private String token;
    private Timestamp createdAt;
}
