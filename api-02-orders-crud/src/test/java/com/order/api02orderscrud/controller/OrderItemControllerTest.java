package com.order.api02orderscrud.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.api02orderscrud.dto.OrderItemDTO;
import com.order.api02orderscrud.exception.EntityNotFoundException;
import com.order.api02orderscrud.exception.GlobalExceptionHandler;
import com.order.api02orderscrud.service.OrderItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <h1>OrderItemControllerTest</h1>
 * <p>Verifies the MVC slice behavior of {@link OrderItemController} for item listing, creation, validation, and not-found flows under Spring Boot 4.</p>
 */
@WebMvcTest(OrderItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderItemService orderItemService;

    @Test
    @WithMockUser
    void findByOrderId_WithValidId_ShouldReturnList() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderItemDTO dto = new OrderItemDTO(UUID.randomUUID(), "Product 1", 2, new BigDecimal("50.00"), new BigDecimal("100.00"));

        when(orderItemService.findByOrderId(orderId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/orders/{orderId}/items", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Product 1"));
    }

    @Test
    @WithMockUser
    void addItem_WithValidData_ShouldReturn201() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderItemDTO dto = new OrderItemDTO(null, "Product 1", 2, new BigDecimal("50.00"), null);
        OrderItemDTO savedDto = new OrderItemDTO(UUID.randomUUID(), "Product 1", 2, new BigDecimal("50.00"), new BigDecimal("100.00"));

        when(orderItemService.addItem(eq(orderId), any(OrderItemDTO.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.subtotal").value(100.00));
    }

    @Test
    @WithMockUser
    void addItem_WhenOrderNotFound_ShouldReturn404() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderItemDTO dto = new OrderItemDTO(null, "Product 1", 2, new BigDecimal("50.00"), null);

        when(orderItemService.addItem(eq(orderId), any(OrderItemDTO.class)))
                .thenThrow(new EntityNotFoundException("Order not found"));

        mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void addItem_WithInvalidData_ShouldReturn400() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderItemDTO dto = new OrderItemDTO(null, "", 0, new BigDecimal("-1.00"), null);

        mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
