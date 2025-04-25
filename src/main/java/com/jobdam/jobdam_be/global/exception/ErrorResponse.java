package com.jobdam.jobdam_be.global.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private int code;
    private String message;
}