package com.order.api01authgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(description = "User structural details")
public record UserResponse(
    @Schema(description = "Database user ID", example = "1")
    Integer id, 
    
    @Schema(description = "Username", example = "admin")
    String username, 
    
    @Schema(description = "Authorization roles", example = "[\"USER\", \"ADMIN\"]")
    Set<String> roles
) {}
