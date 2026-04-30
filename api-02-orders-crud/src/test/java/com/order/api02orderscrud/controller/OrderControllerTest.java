package com.order.api02orderscrud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.order.api02orderscrud.dto.OrderDTO;
import com.order.api02orderscrud.entity.OrderStatus;
import com.order.api02orderscrud.exception.EntityNotFoundException;
import com.order.api02orderscrud.exception.GlobalExceptionHandler;
import com.order.api02orderscrud.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <h1>OrderControllerTest</h1>
 * <p>Verifies the MVC slice behavior of {@link OrderController} for create, list, and not-found flows under Spring Boot 3.</p>
 */
@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    @MockBean
    private OrderService orderService;

    @Test
    @WithMockUser
    void save_WithValidData_ShouldReturn201() throws Exception {
        OrderDTO dto = new OrderDTO(null, "John Doe", "john@example.com", LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO, List.of());
        OrderDTO savedDto = new OrderDTO(UUID.randomUUID(), "John Doe", "john@example.com", LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO, List.of());

        when(orderService.save(any(OrderDTO.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerName").value("John Doe"));
    }

    @Test
    @WithMockUser
    void save_WithMissingMandatoryFields_ShouldReturn400() throws Exception {
        OrderDTO dto = new OrderDTO(null, "", "invalid-email", null, null, null, List.of());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void findAll_ShouldReturnPagedOrders() throws Exception {
        OrderDTO dto = new OrderDTO(UUID.randomUUID(), "John Doe", "john@example.com", LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO, List.of());
        Page<OrderDTO> page = new PageImpl<>(List.of(dto));

        when(orderService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/orders")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].customerName").value("John Doe"));
    }

    @Test
    @WithMockUser
    void findById_WhenNotFound_ShouldReturn404() throws Exception {
        UUID id = UUID.randomUUID();
        when(orderService.findById(id)).thenThrow(new EntityNotFoundException("Order not found"));

        mockMvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void update_WithValidData_ShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        OrderDTO dto = new OrderDTO(null, "John Doe", "john@example.com", LocalDateTime.now(), OrderStatus.CONFIRMED, BigDecimal.TEN, List.of());
        OrderDTO updatedDto = new OrderDTO(id, "John Doe", "john@example.com", dto.orderDate(), OrderStatus.CONFIRMED, BigDecimal.TEN, List.of());

        when(orderService.update(any(UUID.class), any(OrderDTO.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/orders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser
    void update_WhenNotFound_ShouldReturn404() throws Exception {
        UUID id = UUID.randomUUID();
        OrderDTO dto = new OrderDTO(null, "John Doe", "john@example.com", LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO, List.of());

        when(orderService.update(any(UUID.class), any(OrderDTO.class))).thenThrow(new EntityNotFoundException("Order not found"));

        mockMvc.perform(put("/api/orders/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}
