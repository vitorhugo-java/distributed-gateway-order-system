package com.order.api01authgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing the access token")
public record TokenResponse(
    @Schema(description = "Encrypted JWT token", example = "eyJhbGciOiJIUzI1NiIsInR...")
    String token, 
    
    @Schema(description = "Token type", example = "Bearer")
    String type, 
    
    @Schema(description = "Expiration time in milliseconds", example = "3600000")
    Long expiresIn
) {}