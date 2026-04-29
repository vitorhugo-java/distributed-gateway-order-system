package com.order.api01authgateway.controller;

import com.order.api01authgateway.dto.OrderDTO;
import com.order.api01authgateway.dto.OrderItemDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Proxy para gerenciamento de pedidos e itens (API-02)")
@SecurityRequirement(name = "bearerAuth")
public class OrderProxyController {

    private final WebClient webClient;

    // ─── Orders ───────────────────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Retorna uma lista paginada de todos os pedidos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public Mono<ResponseEntity<byte[]>> findAll(HttpServletRequest request,
                                                @RequestHeader HttpHeaders headers) {
        return proxy(request, headers, null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Retorna os detalhes de um pedido específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public Mono<ResponseEntity<byte[]>> findById(
            @Parameter(description = "ID do pedido") @PathVariable UUID id,
            HttpServletRequest request,
            @RequestHeader HttpHeaders headers) {
        return proxy(request, headers, null);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar novo pedido", description = "Cria um novo pedido no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public Mono<ResponseEntity<byte[]>> save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do pedido") @RequestBody OrderDTO dto,
            HttpServletRequest request,
            @RequestHeader HttpHeaders headers) {
        return proxyWithBody(request, headers, dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pedido", description = "Atualiza os dados de um pedido existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public Mono<ResponseEntity<byte[]>> update(
            @Parameter(description = "ID do pedido") @PathVariable UUID id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados atualizados do pedido") @RequestBody OrderDTO dto,
            HttpServletRequest request,
            @RequestHeader HttpHeaders headers) {
        return proxyWithBody(request, headers, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pedido", description = "Remove um pedido pelo seu ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public Mono<ResponseEntity<byte[]>> delete(
            @Parameter(description = "ID do pedido") @PathVariable UUID id,
            HttpServletRequest request,
            @RequestHeader HttpHeaders headers) {
        return proxy(request, headers, null);
    }

    // ─── Order Items ──────────────────────────────────────────────────────

    @GetMapping("/{orderId}/items")
    @Operation(summary = "Listar itens do pedido", description = "Retorna a lista de itens associados a um pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public Mono<ResponseEntity<byte[]>> findItemsByOrderId(
            @Parameter(description = "ID do pedido") @PathVariable UUID orderId,
            HttpServletRequest request,
            @RequestHeader HttpHeaders headers) {
        return proxy(request, headers, null);
    }

    @PostMapping("/{orderId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adicionar item ao pedido", description = "Adiciona um novo item ao pedido especificado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Adicionado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    public Mono<ResponseEntity<byte[]>> addItem(
            @Parameter(description = "ID do pedido") @PathVariable UUID orderId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do item") @RequestBody OrderItemDTO dto,
            HttpServletRequest request,
            @RequestHeader HttpHeaders headers) {
        return proxyWithBody(request, headers, dto);
    }

    // ─── Internal proxy helpers ───────────────────────────────────────────

    private Mono<ResponseEntity<byte[]>> proxy(HttpServletRequest request, HttpHeaders headers, byte[] body) {
        String path = request.getRequestURI().replaceFirst("^/api/orders", "");
        String query = request.getQueryString() != null ? "?" + request.getQueryString() : "";

        WebClient.RequestBodySpec bodySpec = webClient
                .method(HttpMethod.valueOf(request.getMethod()))
                .uri(path + query)
                .headers(h -> {
                    h.addAll(headers);
                    h.remove(HttpHeaders.HOST);
                });

        if (body != null) {
            return bodySpec.bodyValue(body).exchangeToMono(response -> response.toEntity(byte[].class));
        }
        return bodySpec.exchangeToMono(response -> response.toEntity(byte[].class));
    }

    private Mono<ResponseEntity<byte[]>> proxyWithBody(HttpServletRequest request, HttpHeaders headers, Object dto) {
        String path = request.getRequestURI().replaceFirst("^/api/orders", "");
        String query = request.getQueryString() != null ? "?" + request.getQueryString() : "";

        return webClient
                .method(HttpMethod.valueOf(request.getMethod()))
                .uri(path + query)
                .headers(h -> {
                    h.addAll(headers);
                    h.remove(HttpHeaders.HOST);
                })
                .bodyValue(dto)
                .exchangeToMono(response -> response.toEntity(byte[].class));
    }
}