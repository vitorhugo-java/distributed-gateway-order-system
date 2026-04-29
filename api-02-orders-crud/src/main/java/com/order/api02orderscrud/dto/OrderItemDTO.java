package com.order.api02orderscrud.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Objeto de transferência de dados de item de pedido")
public record OrderItemDTO(
        @Schema(description = "ID do item", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
        @NotBlank @Schema(description = "Nome do produto", example = "Monitor Gamer") String productName,
        @NotNull @Min(1) @Schema(description = "Quantidade", example = "2") Integer quantity,
        @NotNull @Positive @Schema(description = "Preço unitário", example = "1200.00") BigDecimal unitPrice,
        @Schema(description = "Subtotal do item", example = "2400.00") BigDecimal subtotal
) {}
