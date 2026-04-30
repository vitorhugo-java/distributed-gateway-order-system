package com.order.api02orderscrud.controller;

import com.order.api02orderscrud.dto.OrderDTO;
import com.order.api02orderscrud.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Gerenciamento de pedidos e itens")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Retorna uma lista paginada de todos os pedidos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public Page<OrderDTO> findAll(@ParameterObject Pageable pageable) {
        return orderService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna os detalhes de um pedido específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public OrderDTO findById(@PathVariable UUID id) {
        return orderService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar novo pedido", description = "Cria um novo pedido no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de Validação"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public OrderDTO save(@Valid @RequestBody OrderDTO dto) {
        return orderService.save(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pedido", description = "Atualiza os dados de um pedido existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public OrderDTO update(@PathVariable UUID id, @Valid @RequestBody OrderDTO dto) {
        return orderService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pedido", description = "Remove um pedido pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
