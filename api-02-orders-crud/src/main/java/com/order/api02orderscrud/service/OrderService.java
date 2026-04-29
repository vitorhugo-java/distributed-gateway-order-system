package com.order.api02orderscrud.service;

import com.order.api02orderscrud.dto.OrderDTO;
import com.order.api02orderscrud.dto.OrderItemDTO;
import com.order.api02orderscrud.entity.Order;
import com.order.api02orderscrud.entity.OrderItem;
import com.order.api02orderscrud.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public Page<OrderDTO> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(o -> new OrderDTO(o.getId(), o.getCustomerName(), o.getCustomerEmail(), o.getOrderDate(), o.getStatus(), o.getTotalAmount()));
    }

    @Transactional(readOnly = true)
    public OrderDTO findById(UUID id) {
        Order o = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderDTO(o.getId(), o.getCustomerName(), o.getCustomerEmail(), o.getOrderDate(), o.getStatus(), o.getTotalAmount());
    }

    @Transactional
    public OrderDTO save(OrderDTO dto) {
        Order o = new Order();
        o.setCustomerName(dto.customerName());
        o.setCustomerEmail(dto.customerEmail());
        o.setOrderDate(dto.orderDate());
        o.setStatus(dto.status());
        o.setTotalAmount(java.math.BigDecimal.ZERO);
        o = orderRepository.save(o);
        return new OrderDTO(o.getId(), o.getCustomerName(), o.getCustomerEmail(), o.getOrderDate(), o.getStatus(), o.getTotalAmount());
    }

    @Transactional
    public void delete(UUID id) {
        orderRepository.deleteById(id);
    }
}
