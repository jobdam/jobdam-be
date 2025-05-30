package com.jobdam.jobdam_be.clova.dto;

import java.util.List;

public record ChatRequest(List<Message> messages, int maxTokens) {
}