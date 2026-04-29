package com.order.api02orderscrud.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Objeto de transferência de dados do Item do Pedido")
public record OrderItemDTO(
        @Schema(description = "ID do item", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
        @Schema(description = "Nome do produto", example = "Notebook") String productName,
        @Schema(description = "Quantidade", example = "1") Integer quantity,
        @Schema(description = "Preço unitário", example = "5000.00") BigDecimal unitPrice,
        @Schema(description = "Subtotal do item", example = "5000.00") BigDecimal subtotal
) {}
