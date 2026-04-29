package com.order.api02orderscrud.service;

import com.order.api02orderscrud.dto.OrderItemDTO;
import com.order.api02orderscrud.entity.Order;
import com.order.api02orderscrud.entity.OrderItem;
import com.order.api02orderscrud.repository.OrderItemRepository;
import com.order.api02orderscrud.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public OrderItemService(OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<OrderItemDTO> findByOrderId(UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        return order.getItems().stream()
                .map(i -> new OrderItemDTO(i.getId(), i.getProductName(), i.getQuantity(), i.getUnitPrice(), i.getSubtotal()))
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderItemDTO addItem(UUID orderId, OrderItemDTO dto) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        OrderItem item = new OrderItem();
        item.setProductName(dto.productName());
        item.setQuantity(dto.quantity());
        item.setUnitPrice(dto.unitPrice());
        order.addItem(item);
        orderRepository.save(order);
        return new OrderItemDTO(item.getId(), item.getProductName(), item.getQuantity(), item.getUnitPrice(), item.getSubtotal());
    }
}
