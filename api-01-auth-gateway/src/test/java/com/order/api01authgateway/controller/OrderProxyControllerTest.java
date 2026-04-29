package com.order.api01authgateway.controller;

import com.order.api01authgateway.security.JwtService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * <h1>OrderProxyControllerTest</h1>
 * <p>
 * Tests the {@link OrderProxyController} by simulating a downstream Orders API using
 * {@link MockWebServer}. Ensures requests are correctly proxied and responses are
 * properly returned to clients without loading the full application context.
 * </p>
 * <p>
 * Implements the WebMvcTest slice pattern with Spring Boot 4.0.x and Spring Security 7.x.
 * </p>
 */
@WebMvcTest(OrderProxyController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(OrderProxyControllerTest.OrderProxyTestConfiguration.class)
public class OrderProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    private static MockWebServer mockWebServer;

    /**
     * Internal test configuration providing a {@link WebClient} bean that points to
     * the {@link MockWebServer} instance for intercepting downstream API calls.
     */
    @TestConfiguration
    static class OrderProxyTestConfiguration {
        @Bean
        public WebClient webClient() {
            return WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
        }
    }

    /**
     * Initializes and starts the {@link MockWebServer} before all tests execute.
     *
     * @throws IOException if the server fails to initialize
     */
    @BeforeAll
    public static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    /**
     * Gracefully shuts down the {@link MockWebServer} after all tests complete.
     *
     * @throws IOException if the server fails to shut down
     */
    @AfterAll
    public static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    /**
     * Validates that incoming HTTP GET requests are correctly proxied to the
     * downstream {@link MockWebServer} and responses are returned to the user.
     *
     * @throws Exception if any error occurs during request execution
     */
    @Test
    public void shouldProxyRequest() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("proxied"));

        var mvcResult = mockMvc.perform(get("/api/orders/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string("proxied"));
    }
}
