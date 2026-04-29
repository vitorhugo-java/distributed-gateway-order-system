package com.order.api02orderscrud.controller;

import com.order.api02orderscrud.dto.OrderItemDTO;
import com.order.api02orderscrud.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders/{orderId}/items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    public List<OrderItemDTO> findByOrderId(@PathVariable UUID orderId) {
        return orderItemService.findByOrderId(orderId);
    }

    @PostMapping
    public OrderItemDTO addItem(@PathVariable UUID orderId, @RequestBody OrderItemDTO dto) {
        return orderItemService.addItem(orderId, dto);
    }
}
