package com.order.api02orderscrud.controller;

import com.order.api02orderscrud.dto.OrderItemDTO;
import com.order.api02orderscrud.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders/{orderId}/items")
@RequiredArgsConstructor
@Tag(name = "Order Items", description = "Gerenciamento de itens de pedido")
@SecurityRequirement(name = "bearerAuth")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    @Operation(summary = "Listar itens do pedido", description = "Retorna a lista de itens associados a um pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public List<OrderItemDTO> findByOrderId(@PathVariable UUID orderId) {
        return orderItemService.findByOrderId(orderId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adicionar item ao pedido", description = "Adiciona um novo item ao pedido especificado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de Validação"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public OrderItemDTO addItem(@PathVariable UUID orderId, @Valid @RequestBody OrderItemDTO dto) {
        return orderItemService.addItem(orderId, dto);
    }
}
