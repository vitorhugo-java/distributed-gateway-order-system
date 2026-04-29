package com.order.api02orderscrud.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDTO(UUID id, String productName, Integer quantity, BigDecimal unitPrice, BigDecimal subtotal) {}
