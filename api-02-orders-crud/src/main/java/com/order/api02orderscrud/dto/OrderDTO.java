package com.order.api02orderscrud.dto;

import com.order.api02orderscrud.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Objeto de transferência de dados do Pedido")
public record OrderDTO(
        @Schema(description = "ID do pedido", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
        @Schema(description = "Nome do cliente", example = "João Silva") String customerName,
        @Schema(description = "Email do cliente", example = "joao@example.com") String customerEmail,
        @Schema(description = "Data do pedido", example = "2026-04-28T10:00:00") LocalDateTime orderDate,
        @Schema(description = "Status do pedido", example = "PENDING") OrderStatus status,
        @Schema(description = "Valor total do pedido", example = "150.00") BigDecimal totalAmount
) {}
