package com.order.api01authgateway.dto;

import java.util.Set;

public record UserResponse(Integer id, String name, String email, Set<String> roles) {}
