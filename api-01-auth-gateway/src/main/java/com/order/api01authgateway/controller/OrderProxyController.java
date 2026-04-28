package com.order.api01authgateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderProxyController {

    private final WebClient webClient;

    @RequestMapping("/**")
    public Mono<ResponseEntity<byte[]>> proxy(HttpServletRequest request) {
        String path = request.getRequestURI().replace("/api/orders", "");
        String query = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        return webClient.method(org.springframework.http.HttpMethod.valueOf(request.getMethod()))
                .uri(path + query)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .retrieve()
                .toEntity(byte[].class);
    }
}
