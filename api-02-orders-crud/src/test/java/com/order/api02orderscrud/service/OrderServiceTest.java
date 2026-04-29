package com.order.api02orderscrud.service;

import com.order.api02orderscrud.dto.OrderDTO;
import com.order.api02orderscrud.entity.Order;
import com.order.api02orderscrud.entity.OrderStatus;
import com.order.api02orderscrud.mapper.OrderMapper;
import com.order.api02orderscrud.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void testFindById() {
        UUID id = UUID.randomUUID();
        Order o = new Order();
        o.setId(id);
        o.setCustomerName("John");
        o.setTotalAmount(BigDecimal.TEN);
        OrderDTO dto = new OrderDTO(id, "John", "john@example.com", LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.TEN);
        when(orderRepository.findById(id)).thenReturn(Optional.of(o));
        when(orderMapper.toDto(o)).thenReturn(dto);

        OrderDTO result = orderService.findById(id);

        assertEquals("John", result.customerName());
        assertEquals(BigDecimal.TEN, result.totalAmount());
    }
}
