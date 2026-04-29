package com.order.api02orderscrud.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) that represents an order item.
 */
@Schema(description = "Order Item Data Transfer Object")
public record OrderItemDTO(
        @Schema(description = "Item ID", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
        @Schema(description = "Product name", example = "Laptop") String productName,
        @Schema(description = "Quantity", example = "1") Integer quantity,
        @Schema(description = "Unit price", example = "5000.00") BigDecimal unitPrice,
        @Schema(description = "Item subtotal", example = "5000.00") BigDecimal subtotal
) {}
