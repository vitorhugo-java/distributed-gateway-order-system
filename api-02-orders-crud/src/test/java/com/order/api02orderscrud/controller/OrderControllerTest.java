package com.order.api02orderscrud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.api02orderscrud.dto.OrderDTO;
import com.order.api02orderscrud.entity.OrderStatus;
import com.order.api02orderscrud.exception.EntityNotFoundException;
import com.order.api02orderscrud.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    @WithMockUser
    void save_WithValidData_ShouldReturn201() throws Exception {
        OrderDTO dto = new OrderDTO(null, "John Doe", "john@example.com", LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO);
        OrderDTO savedDto = new OrderDTO(UUID.randomUUID(), "John Doe", "john@example.com", LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO);

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
        OrderDTO dto = new OrderDTO(null, "", "invalid-email", null, null, null);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void findAll_ShouldReturnPagedOrders() throws Exception {
        OrderDTO dto = new OrderDTO(UUID.randomUUID(), "John Doe", "john@example.com", LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO);
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
}
