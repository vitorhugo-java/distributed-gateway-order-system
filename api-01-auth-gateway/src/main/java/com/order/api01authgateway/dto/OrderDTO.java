package com.order.api01authgateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Objeto de transferência de dados de pedido")
public record OrderDTO(
        @Schema(description = "ID do pedido", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
        @NotBlank @Schema(description = "Nome do cliente", example = "João Silva") String customerName,
        @NotBlank @Email @Schema(description = "E-mail do cliente", example = "joao.silva@example.com") String customerEmail,
        @NotNull @Schema(description = "Data do pedido", example = "2026-04-28T10:00:00") LocalDateTime orderDate,
        @NotNull @Schema(description = "Status do pedido", example = "PENDING") OrderStatus status,
        @NotNull @Schema(description = "Valor total do pedido", example = "150.00") BigDecimal totalAmount,
        @Valid @Schema(description = "Itens do pedido") List<OrderItemDTO> items
) {}
