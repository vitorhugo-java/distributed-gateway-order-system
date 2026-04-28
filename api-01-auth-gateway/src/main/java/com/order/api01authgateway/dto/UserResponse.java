package com.order.api01authgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(description = "User structural details")
public record UserResponse(
    @Schema(description = "Database user ID", example = "1")
    Integer id, 
    
    @Schema(description = "Full name", example = "Vitor Hugo")
    String name, 
    
    @Schema(description = "Contact email", example = "vitor@email.com")
    String email, 
    
    @Schema(description = "Authorization roles", example = "[\"USER\", \"ADMIN\"]")
    Set<String> roles
) {}