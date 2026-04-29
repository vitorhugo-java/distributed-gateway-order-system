package com.order.api02orderscrud.controller;

import com.order.api02orderscrud.dto.OrderItemDTO;
import com.order.api02orderscrud.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller to manage items of an order.
 */
@RestController
@RequestMapping("/api/orders/{orderId}/items")
@RequiredArgsConstructor
@Tag(name = "Order Items", description = "Endpoints for managing order items")
@SecurityRequirement(name = "bearerAuth")
public class OrderItemController {

    private final OrderItemService orderItemService;

    /**
     * Finds all items associated with an order.
     * @param orderId order identifier
     * @return list of found items
     */
    @GetMapping
    @Operation(summary = "List order items", description = "Returns the list of items associated with an order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public List<OrderItemDTO> findByOrderId(@PathVariable UUID orderId) {
        return orderItemService.findByOrderId(orderId);
    }

    /**
     * Adds a new item to the order.
     * @param orderId order identifier
     * @param dto new item DTO
     * @return created item DTO
     */
    @PostMapping
    @Operation(summary = "Add item to order", description = "Adds a new item to the specified order")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public OrderItemDTO addItem(@PathVariable UUID orderId, @RequestBody OrderItemDTO dto) {
        return orderItemService.addItem(orderId, dto);
    }
}
