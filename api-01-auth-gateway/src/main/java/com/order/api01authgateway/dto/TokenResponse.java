package com.order.api01authgateway.dto;

public record TokenResponse(String token, String type, Long expiresIn) {}
