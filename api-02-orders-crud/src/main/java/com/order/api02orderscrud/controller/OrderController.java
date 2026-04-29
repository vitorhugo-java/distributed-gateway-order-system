package com.order.api02orderscrud.controller;

import com.order.api02orderscrud.dto.OrderDTO;
import com.order.api02orderscrud.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller to manage order operations.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Endpoints for order management")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    /**
     * Lists existing orders in a paginated way.
     * @param pageable pagination information
     * @return page containing found orders
     */
    @GetMapping
    @Operation(summary = "List orders", description = "Returns a paginated list of orders")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public Page<OrderDTO> findAll(Pageable pageable) {
        return orderService.findAll(pageable);
    }

    /**
     * Finds an order by its ID.
     * @param id order identifier
     * @return DTO of the found order
     */
    @GetMapping("/{id}")
    @Operation(summary = "Find order by ID", description = "Returns the details of a specific order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public OrderDTO findById(@PathVariable UUID id) {
        return orderService.findById(id);
    }

    /**
     * Creates a new order in the system.
     * @param dto new order DTO
     * @return created order DTO
     */
    @PostMapping
    @Operation(summary = "Create new order", description = "Creates a new order in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public OrderDTO save(@RequestBody OrderDTO dto) {
        return orderService.save(dto);
    }

    /**
     * Deletes an existing order by ID.
     * @param id identifier of the order to be removed
     * @return empty response with noContent status
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order", description = "Removes an order by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
