package com.order.api01authgateway.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Configuration class for setting up resilient {@link WebClient} instances.
 * <p>
 * Defines connectivity parameters, including connection and response timeouts, as well as
 * buffer capacity limits to ensure system stability when communicating with downstream services.
 * </p>
 */
@Configuration
public class WebClientConfig {

    @Value("${application.gateway.downstream-url}")
    private String downstreamUrl;

    /**
     * Creates and configures a {@link WebClient} bean with predefined resilience settings.
     *
     * @return A configured {@link WebClient} instance.
     */
    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(10));

        return WebClient.builder()
                .baseUrl(downstreamUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
    }
}