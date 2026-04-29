package com.order.api02orderscrud.dto;

import com.order.api02orderscrud.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderDTO(UUID id, String customerName, String customerEmail, LocalDateTime orderDate, OrderStatus status, BigDecimal totalAmount) {}
