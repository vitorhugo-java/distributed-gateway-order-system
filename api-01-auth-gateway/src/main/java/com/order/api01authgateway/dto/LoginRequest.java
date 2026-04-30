package com.order.api01authgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User authentication payload")
public record LoginRequest(
    @Schema(description = "Registered username", example = "admin")
    @NotBlank String username,
    
    @Schema(description = "User password", example = "admin123")
    @NotBlank String password
) {}
