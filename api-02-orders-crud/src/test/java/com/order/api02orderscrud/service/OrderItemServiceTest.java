package com.order.api02orderscrud.service;

import com.order.api02orderscrud.dto.OrderItemDTO;
import com.order.api02orderscrud.entity.Order;
import com.order.api02orderscrud.entity.OrderItem;
import com.order.api02orderscrud.exception.EntityNotFoundException;
import com.order.api02orderscrud.repository.OrderItemRepository;
import com.order.api02orderscrud.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    @Test
    void addItem_ShouldCalculateSubtotalAndSaveOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setId(orderId);
        
        OrderItemDTO dto = new OrderItemDTO(null, "Product 1", 3, new BigDecimal("100.00"), null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderItemDTO result = orderItemService.addItem(orderId, dto);

        assertThat(result.subtotal()).isEqualByComparingTo(new BigDecimal("300.00"));
        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("300.00"));
        verify(orderRepository).save(order);
    }

    @Test
    void addItem_WhenOrderNotFound_ShouldThrowException() {
        UUID orderId = UUID.randomUUID();
        OrderItemDTO dto = new OrderItemDTO(null, "Product 1", 3, new BigDecimal("100.00"), null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemService.addItem(orderId, dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Order not found");
    }

    @Test
    void findByOrderId_WhenOrderNotFound_ShouldThrowException() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderItemService.findByOrderId(orderId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Order not found");
    }

    @Test
    void addItem_WithMultipleItems_ShouldRecalculateOrderTotal() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setId(orderId);
        
        OrderItem existingItem = new OrderItem();
        existingItem.setProductName("Product 0");
        existingItem.setQuantity(1);
        existingItem.setUnitPrice(new BigDecimal("50.00"));
        order.addItem(existingItem);

        OrderItemDTO dto = new OrderItemDTO(null, "Product 1", 2, new BigDecimal("100.00"), null);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderItemService.addItem(orderId, dto);

        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
        assertThat(order.getItems()).hasSize(2);
    }
}
