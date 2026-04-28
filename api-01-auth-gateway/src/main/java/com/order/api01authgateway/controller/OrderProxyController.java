package com.order.api01authgateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders Proxy", description = "Routing to the Orders API")
public class OrderProxyController {

    private final WebClient webClient;

    @Operation(
        summary = "Dynamic proxy for Orders", 
        description = "Forwards all requests to the Orders microservice encapsulating the JWT",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @RequestMapping("/**")
    public Mono<ResponseEntity<byte[]>> proxy(
            HttpServletRequest request,
            @RequestHeader HttpHeaders headers,
            @RequestBody(required = false) byte[] body) {

        String path = request.getRequestURI().replaceFirst("^/api/orders", "");
        String query = request.getQueryString() != null ? "?" + request.getQueryString() : "";

        WebClient.RequestBodySpec bodySpec = webClient
                .method(HttpMethod.valueOf(request.getMethod()))
                .uri(path + query)
                .headers(httpHeaders -> {
                    httpHeaders.addAll(headers);
                    httpHeaders.remove(HttpHeaders.HOST);
                });

        if (body != null) {
            return bodySpec.bodyValue(body).exchangeToMono(response -> response.toEntity(byte[].class));
        } else {
            return bodySpec.exchangeToMono(response -> response.toEntity(byte[].class));
        }
    }
}