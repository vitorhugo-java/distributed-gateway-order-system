package com.order.api02orderscrud.service;

import com.order.api02orderscrud.dto.OrderItemDTO;
import com.order.api02orderscrud.entity.Order;
import com.order.api02orderscrud.exception.EntityNotFoundException;
import com.order.api02orderscrud.mapper.OrderMapper;
import com.order.api02orderscrud.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public List<OrderItemDTO> findByOrderId(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return order.getItems().stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Transactional
    public OrderItemDTO addItem(UUID orderId, OrderItemDTO dto) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
        var item = orderMapper.toEntity(dto);
        order.addItem(item);
        orderRepository.save(order);
        return orderMapper.toDto(item);
    }
}
