package com.order.api01authgateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${application.gateway.downstream-url}")
    private String downstreamUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(downstreamUrl)
                .build();
    }
}
