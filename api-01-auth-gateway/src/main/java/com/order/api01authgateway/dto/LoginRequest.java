package com.order.api01authgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User authentication payload")
public record LoginRequest(
    @Schema(description = "Registered email", example = "admin@cotefacil.com")
    @NotBlank @Email String email,
    
    @Schema(description = "User password", example = "strongPassword123")
    @NotBlank String password
) {}