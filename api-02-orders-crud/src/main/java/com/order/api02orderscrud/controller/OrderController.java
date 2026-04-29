package com.order.api02orderscrud.controller;

import com.order.api02orderscrud.dto.OrderDTO;
import com.order.api02orderscrud.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Page<OrderDTO> findAll(Pageable pageable) {
        return orderService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public OrderDTO findById(@PathVariable UUID id) {
        return orderService.findById(id);
    }

    @PostMapping
    public OrderDTO save(@RequestBody OrderDTO dto) {
        return orderService.save(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
