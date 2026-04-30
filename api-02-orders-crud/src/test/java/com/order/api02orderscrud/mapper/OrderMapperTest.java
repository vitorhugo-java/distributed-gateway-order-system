package com.order.api02orderscrud.mapper;

import com.order.api02orderscrud.dto.OrderDTO;
import com.order.api02orderscrud.dto.OrderItemDTO;
import com.order.api02orderscrud.entity.Order;
import com.order.api02orderscrud.entity.OrderItem;
import com.order.api02orderscrud.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * <h1>OrderMapperTest</h1>
 * <p>Validates DTO and entity conversion branches in {@link OrderMapper}.</p>
 */
class OrderMapperTest {

    private final OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void shouldMapOrderEntityToDto() {
        Order order = new Order();
        UUID id = UUID.randomUUID();
        LocalDateTime dateTime = LocalDateTime.now();
        order.setId(id);
        order.setCustomerName("John");
        order.setCustomerEmail("john@example.com");
        order.setOrderDate(dateTime);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("150.00"));

        OrderDTO dto = mapper.toDto(order);

        assertEquals(id, dto.id());
        assertEquals("John", dto.customerName());
        assertEquals("john@example.com", dto.customerEmail());
        assertEquals(dateTime, dto.orderDate());
        assertEquals(OrderStatus.PENDING, dto.status());
        assertEquals(new BigDecimal("150.00"), dto.totalAmount());
        assertEquals(0, dto.items().size());
    }

    @Test
    void shouldMapOrderDtoToEntityIgnoringIdAndItems() {
        OrderDTO dto = new OrderDTO(
                UUID.randomUUID(),
                "John",
                "john@example.com",
                LocalDateTime.now(),
                OrderStatus.PENDING,
                new BigDecimal("100.00"),
                List.of()
        );

        Order entity = mapper.toEntity(dto);

        assertNull(entity.getId());
        assertEquals("John", entity.getCustomerName());
        assertEquals("john@example.com", entity.getCustomerEmail());
        assertEquals(OrderStatus.PENDING, entity.getStatus());
        assertEquals(new BigDecimal("100.00"), entity.getTotalAmount());
    }

    @Test
    void shouldMapOrderItemEntityToDto() {
        OrderItem item = new OrderItem();
        UUID id = UUID.randomUUID();
        item.setId(id);
        item.setProductName("Product 1");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("10.00"));

        OrderItemDTO dto = mapper.toDto(item);

        assertEquals(id, dto.id());
        assertEquals("Product 1", dto.productName());
        assertEquals(2, dto.quantity());
        assertEquals(new BigDecimal("10.00"), dto.unitPrice());
        assertEquals(new BigDecimal("20.00"), dto.subtotal());
    }

    @Test
    void shouldMapOrderItemDtoToEntityIgnoringIdAndOrder() {
        OrderItemDTO dto = new OrderItemDTO(UUID.randomUUID(), "Product 1", 3, new BigDecimal("15.00"), new BigDecimal("45.00"));

        OrderItem entity = mapper.toEntity(dto);

        assertNull(entity.getId());
        assertNull(entity.getOrder());
        assertEquals("Product 1", entity.getProductName());
        assertEquals(3, entity.getQuantity());
        assertEquals(new BigDecimal("15.00"), entity.getUnitPrice());
        assertEquals(new BigDecimal("45.00"), entity.getSubtotal());
    }

    @Test
    void shouldReturnNullWhenOrderEntityIsNull() {
        assertNull(mapper.toDto((Order) null));
    }

    @Test
    void shouldReturnNullWhenOrderDtoIsNull() {
        assertNull(mapper.toEntity((OrderDTO) null));
    }

    @Test
    void shouldReturnNullWhenOrderItemEntityIsNull() {
        assertNull(mapper.toDto((OrderItem) null));
    }

    @Test
    void shouldReturnNullWhenOrderItemDtoIsNull() {
        assertNull(mapper.toEntity((OrderItemDTO) null));
    }
}
