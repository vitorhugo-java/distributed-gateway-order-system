package com.order.api02orderscrud.service;

import com.order.api02orderscrud.dto.OrderItemDTO;
import com.order.api02orderscrud.entity.Order;
import com.order.api02orderscrud.entity.OrderItem;
import com.order.api02orderscrud.exception.EntityNotFoundException;
import com.order.api02orderscrud.mapper.OrderMapper;
import com.order.api02orderscrud.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <h1>OrderItemServiceTest</h1>
 * <p>Validates business rules and failure scenarios for {@link OrderItemService}.</p>
 */
@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderItemService orderItemService;

    @Test
    void addItem_ShouldCalculateSubtotalAndSaveOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setId(orderId);
        
        OrderItemDTO dto = new OrderItemDTO(null, "Product 1", 3, new BigDecimal("100.00"), null);
        OrderItem mappedItem = new OrderItem();
        mappedItem.setProductName(dto.productName());
        mappedItem.setQuantity(dto.quantity());
        mappedItem.setUnitPrice(dto.unitPrice());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toEntity(dto)).thenReturn(mappedItem);
        when(orderMapper.toDto(mappedItem)).thenAnswer(invocation -> {
            OrderItem item = invocation.getArgument(0);
            return new OrderItemDTO(item.getId(), item.getProductName(), item.getQuantity(), item.getUnitPrice(), item.getSubtotal());
        });
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

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> orderItemService.addItem(orderId, dto));

        assertEquals("Order not found", ex.getMessage());
    }

    @Test
    void findByOrderId_WhenOrderNotFound_ShouldThrowException() {
        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> orderItemService.findByOrderId(orderId));

        assertEquals("Order not found", ex.getMessage());
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
        OrderItem mappedItem = new OrderItem();
        mappedItem.setProductName(dto.productName());
        mappedItem.setQuantity(dto.quantity());
        mappedItem.setUnitPrice(dto.unitPrice());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toEntity(dto)).thenReturn(mappedItem);
        when(orderMapper.toDto(mappedItem)).thenAnswer(invocation -> {
            OrderItem itemArgument = invocation.getArgument(0);
            return new OrderItemDTO(itemArgument.getId(), itemArgument.getProductName(), itemArgument.getQuantity(), itemArgument.getUnitPrice(), itemArgument.getSubtotal());
        });
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderItemService.addItem(orderId, dto);

        assertThat(order.getTotalAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
        assertThat(order.getItems()).hasSize(2);
    }

    @Test
    void findByOrderId_ShouldMapExistingItems() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setId(orderId);
        OrderItem item = new OrderItem();
        item.setProductName("Product 1");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("10.00"));
        order.addItem(item);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(item)).thenReturn(new OrderItemDTO(item.getId(), item.getProductName(), item.getQuantity(), item.getUnitPrice(), item.getSubtotal()));

        List<OrderItemDTO> result = orderItemService.findByOrderId(orderId);

        assertEquals(1, result.size());
        assertEquals("Product 1", result.get(0).productName());
        assertThat(result.get(0).subtotal()).isEqualByComparingTo(new BigDecimal("20.00"));
    }
}
