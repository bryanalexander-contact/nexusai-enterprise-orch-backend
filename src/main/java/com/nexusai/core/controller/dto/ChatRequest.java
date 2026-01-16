package com.nexusai.core.controller.dto;

public record ChatRequest(
    Long userId,
    String message
) {}