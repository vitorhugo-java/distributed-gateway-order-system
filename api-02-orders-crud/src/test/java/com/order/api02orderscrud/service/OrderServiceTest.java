package com.order.api02orderscrud.service;

import com.order.api02orderscrud.dto.OrderDTO;
import com.order.api02orderscrud.entity.Order;
import com.order.api02orderscrud.entity.OrderStatus;
import com.order.api02orderscrud.exception.EntityNotFoundException;
import com.order.api02orderscrud.mapper.OrderMapper;
import com.order.api02orderscrud.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <h1>OrderServiceTest</h1>
 * <p>Validates all primary branches of {@link OrderService} including list, save, and delete operations.</p>
 */
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

    @Test
    public void testFindByIdWhenMissingShouldThrow() {
        UUID id = UUID.randomUUID();
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> orderService.findById(id));

        assertEquals("Order not found", ex.getMessage());
    }

    @Test
    public void testFindAllShouldMapPage() {
        Order order = new Order();
        UUID id = UUID.randomUUID();
        order.setId(id);
        order.setCustomerName("John");
        OrderDTO dto = new OrderDTO(id, "John", "john@example.com", LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.TEN);
        Page<Order> page = new PageImpl<>(List.of(order));

        when(orderRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);
        when(orderMapper.toDto(order)).thenReturn(dto);

        Page<OrderDTO> result = orderService.findAll(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).customerName());
    }

    @Test
    public void testSaveShouldMapAndPersist() {
        UUID id = UUID.randomUUID();
        OrderDTO dto = new OrderDTO(null, "John", "john@example.com", LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO);
        Order mapped = new Order();
        mapped.setCustomerName("John");
        Order saved = new Order();
        saved.setId(id);
        saved.setCustomerName("John");
        OrderDTO savedDto = new OrderDTO(id, "John", "john@example.com", LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO);

        when(orderMapper.toEntity(dto)).thenReturn(mapped);
        when(orderRepository.save(mapped)).thenReturn(saved);
        when(orderMapper.toDto(saved)).thenReturn(savedDto);

        OrderDTO result = orderService.save(dto);

        assertEquals(id, result.id());
        assertEquals("John", result.customerName());
    }

    @Test
    public void testUpdateShouldPersistChanges() {
        UUID id = UUID.randomUUID();
        LocalDateTime orderDate = LocalDateTime.now();
        OrderDTO dto = new OrderDTO(null, "Jane", "jane@example.com", orderDate, OrderStatus.CONFIRMED, BigDecimal.TEN);

        Order existing = new Order();
        existing.setId(id);
        existing.setCustomerName("John");
        existing.setCustomerEmail("john@example.com");
        existing.setOrderDate(orderDate.minusDays(1));
        existing.setStatus(OrderStatus.PENDING);
        existing.setTotalAmount(BigDecimal.ONE);

        OrderDTO updatedDto = new OrderDTO(id, "Jane", "jane@example.com", orderDate, OrderStatus.CONFIRMED, BigDecimal.TEN);

        when(orderRepository.findById(id)).thenReturn(Optional.of(existing));
        when(orderRepository.save(existing)).thenReturn(existing);
        when(orderMapper.toDto(existing)).thenReturn(updatedDto);

        OrderDTO result = orderService.update(id, dto);

        assertEquals(id, result.id());
        assertEquals("Jane", result.customerName());
        assertEquals("jane@example.com", result.customerEmail());
        assertEquals(OrderStatus.CONFIRMED, result.status());
        assertEquals(BigDecimal.TEN, result.totalAmount());
    }

    @Test
    public void testUpdateWhenMissingShouldThrow() {
        UUID id = UUID.randomUUID();
        OrderDTO dto = new OrderDTO(null, "Jane", "jane@example.com", LocalDateTime.now(), OrderStatus.CONFIRMED, BigDecimal.TEN);
        when(orderRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> orderService.update(id, dto));

        assertEquals("Order not found", ex.getMessage());
    }

    @Test
    public void testDeleteShouldRemoveWhenExists() {
        UUID id = UUID.randomUUID();
        when(orderRepository.existsById(id)).thenReturn(true);

        orderService.delete(id);

        verify(orderRepository).deleteById(id);
    }

    @Test
    public void testDeleteWhenMissingShouldThrow() {
        UUID id = UUID.randomUUID();
        when(orderRepository.existsById(id)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> orderService.delete(id));

        assertEquals("Order not found", ex.getMessage());
        verify(orderRepository, org.mockito.Mockito.never()).deleteById(any(UUID.class));
    }
}
