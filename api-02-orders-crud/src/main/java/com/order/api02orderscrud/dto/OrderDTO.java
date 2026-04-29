package com.order.api02orderscrud.dto;

import com.order.api02orderscrud.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) that represents an order.
 */
@Schema(description = "Order Data Transfer Object")
public record OrderDTO(
        @Schema(description = "Order ID", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
        @Schema(description = "Customer name", example = "John Doe") String customerName,
        @Schema(description = "Customer email", example = "john@example.com") String customerEmail,
        @Schema(description = "Order date", example = "2026-04-28T10:00:00") LocalDateTime orderDate,
        @Schema(description = "Order status", example = "PENDING") OrderStatus status,
        @Schema(description = "Total order amount", example = "150.00") BigDecimal totalAmount
) {}
