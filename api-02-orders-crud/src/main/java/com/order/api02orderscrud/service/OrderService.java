package com.order.api02orderscrud.service;

import com.order.api02orderscrud.dto.OrderDTO;
import com.order.api02orderscrud.entity.Order;
import com.order.api02orderscrud.entity.OrderItem;
import com.order.api02orderscrud.exception.EntityNotFoundException;
import com.order.api02orderscrud.mapper.OrderMapper;
import com.order.api02orderscrud.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    @Transactional(readOnly = true)
    public OrderDTO findById(UUID id) {
        return orderRepository.findById(id)
                .map(orderMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    @Transactional
    public OrderDTO save(OrderDTO dto) {
        Order order = orderMapper.toEntity(dto);
        normalizeForSave(order);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDTO update(UUID id, OrderDTO dto) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        existingOrder.setCustomerName(dto.customerName());
        existingOrder.setCustomerEmail(dto.customerEmail());
        existingOrder.setOrderDate(dto.orderDate() != null ? dto.orderDate() : existingOrder.getOrderDate());
        existingOrder.setStatus(dto.status());
        existingOrder.setTotalAmount(dto.totalAmount());

        Order updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toDto(updatedOrder);
    }

    @Transactional
    public void delete(UUID id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found");
        }
        orderRepository.deleteById(id);
    }

    private void normalizeForSave(Order order) {
        order.setOrderDate(order.getOrderDate() != null ? order.getOrderDate() : LocalDateTime.now());

        var items = order.getItems() != null ? new ArrayList<>(order.getItems()) : new ArrayList<OrderItem>();
        order.setItems(new ArrayList<>());

        if (items.isEmpty()) {
            order.setTotalAmount(BigDecimal.ZERO);
            return;
        }

        items.forEach(order::addItem);
        order.recalculateTotal();
    }
}
