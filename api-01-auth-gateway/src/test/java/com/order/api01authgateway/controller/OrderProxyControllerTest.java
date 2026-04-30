package com.order.api01authgateway.controller;

import com.order.api01authgateway.dto.OrderDTO;
import com.order.api01authgateway.dto.OrderItemDTO;
import com.order.api01authgateway.security.JwtService;
import okhttp3.mockwebserver.MockResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
 * Implements the WebMvcTest slice pattern with Spring Boot 3.x and Spring Security 6.x.
 * </p>
 */
@WebMvcTest(OrderProxyController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(OrderProxyControllerTest.OrderProxyTestConfiguration.class)
public class OrderProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderProxyController controller;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private UserDetailsService userDetailsService;

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

    @BeforeEach
    void drainRequests() throws InterruptedException {
        while (mockWebServer.takeRequest(10, TimeUnit.MILLISECONDS) != null) {
        }
    }

    /**
     * Validates that incoming HTTP GET requests are correctly proxied to the
     * downstream {@link MockWebServer} and responses are returned to the user.
     *
     * @throws Exception if any error occurs during request execution
     */
    @Test
    public void shouldProxyRequest() throws Exception {
        var orderId = "11111111-1111-1111-1111-111111111111";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("proxied"));

        var mvcResult = mockMvc.perform(get("/api/orders/{id}", orderId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
            .andExpect(request().asyncStarted())
            .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string("proxied"));
    }

            @Test
    public void shouldProxyRequestBody() throws Exception {
            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("created"));

            var mvcResult = mockMvc.perform(post("/api/orders")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                              "customerName":"John Doe",
                              "customerEmail":"john@example.com",
                              "orderDate":"2026-04-29T22:00:00",
                              "status":"PENDING",
                              "totalAmount":10.00,
                              "items":[]
                            }
                            """))
                .andExpect(request().asyncStarted())
                .andReturn();

            mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(content().string("created"));
            }

            @Test
            public void shouldPropagateDownstreamErrorStatus() throws Exception {
            var orderId = "22222222-2222-2222-2222-222222222222";
            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("not-found"));

            var mvcResult = mockMvc.perform(get("/api/orders/{id}", orderId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(request().asyncStarted())
                .andReturn();

            mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNotFound())
                .andExpect(content().string("not-found"));
            }

            @Test
    public void shouldProxyRequestWithQueryString() throws Exception {
            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("query-ok"));

            var mvcResult = mockMvc.perform(get("/api/orders")
                    .param("status", "PENDING")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(request().asyncStarted())
                .andReturn();

            mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string("query-ok"));
            }

            @Test
            public void shouldProxyApiDocsToDownstreamDocsPath() throws Exception {
            mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"openapi\":\"3.1.0\"}"));

            var mvcResult = mockMvc.perform(get("/api/orders/v3/api-docs"))
                .andExpect(request().asyncStarted())
                .andReturn();

            mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"openapi\":\"3.1.0\"}"));

            okhttp3.mockwebserver.RecordedRequest recordedRequest = null;
            okhttp3.mockwebserver.RecordedRequest nextRequest;
            while ((nextRequest = mockWebServer.takeRequest(100, TimeUnit.MILLISECONDS)) != null) {
                recordedRequest = nextRequest;
            }

             org.junit.jupiter.api.Assertions.assertNotNull(recordedRequest);
             org.junit.jupiter.api.Assertions.assertEquals("/v3/api-docs", recordedRequest.getPath());
             }

    @Test
    public void shouldProxyUpdateRequest() throws Exception {
        var orderId = "33333333-3333-3333-3333-333333333333";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("updated"));

        var mvcResult = mockMvc.perform(put("/api/orders/{id}", orderId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderJson()))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string("updated"));

        var recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        org.junit.jupiter.api.Assertions.assertNotNull(recordedRequest);
        org.junit.jupiter.api.Assertions.assertEquals("PUT", recordedRequest.getMethod());
        org.junit.jupiter.api.Assertions.assertEquals("/api/orders/" + orderId, recordedRequest.getPath());
        org.junit.jupiter.api.Assertions.assertEquals("Bearer token", recordedRequest.getHeader(HttpHeaders.AUTHORIZATION));
    }

    @Test
    public void shouldProxyDeleteRequest() throws Exception {
        var orderId = "44444444-4444-4444-4444-444444444444";
        mockWebServer.enqueue(new MockResponse().setResponseCode(204));

        var mvcResult = mockMvc.perform(delete("/api/orders/{id}", orderId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isNoContent());

        var recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        org.junit.jupiter.api.Assertions.assertNotNull(recordedRequest);
        org.junit.jupiter.api.Assertions.assertEquals("DELETE", recordedRequest.getMethod());
        org.junit.jupiter.api.Assertions.assertEquals("/api/orders/" + orderId, recordedRequest.getPath());
        org.junit.jupiter.api.Assertions.assertEquals("Bearer token", recordedRequest.getHeader(HttpHeaders.AUTHORIZATION));
    }

    @Test
    public void shouldProxyFindItemsByOrderIdRequest() throws Exception {
        var orderId = "55555555-5555-5555-5555-555555555555";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("[{\"productName\":\"Keyboard\"}]"));

        var mvcResult = mockMvc.perform(get("/api/orders/{orderId}/items", orderId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"productName\":\"Keyboard\"}]"));

        var recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        org.junit.jupiter.api.Assertions.assertNotNull(recordedRequest);
        org.junit.jupiter.api.Assertions.assertEquals("GET", recordedRequest.getMethod());
        org.junit.jupiter.api.Assertions.assertEquals("/api/orders/" + orderId + "/items", recordedRequest.getPath());
        org.junit.jupiter.api.Assertions.assertEquals("Bearer token", recordedRequest.getHeader(HttpHeaders.AUTHORIZATION));
    }

    @Test
    public void shouldProxyAddItemRequest() throws Exception {
        var orderId = "66666666-6666-6666-6666-666666666666";
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("item-created"));

        var mvcResult = mockMvc.perform(post("/api/orders/{orderId}/items", orderId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderItemJson()))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(content().string("item-created"));

        var recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        org.junit.jupiter.api.Assertions.assertNotNull(recordedRequest);
        org.junit.jupiter.api.Assertions.assertEquals("POST", recordedRequest.getMethod());
        org.junit.jupiter.api.Assertions.assertEquals("/api/orders/" + orderId + "/items", recordedRequest.getPath());
        org.junit.jupiter.api.Assertions.assertEquals("Bearer token", recordedRequest.getHeader(HttpHeaders.AUTHORIZATION));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldProxyViaInternalHelperMethod() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("internal-created"));

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/orders");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token");
        request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        request.setContentType(MediaType.APPLICATION_JSON_VALUE);

        OrderDTO body = new OrderDTO(
                UUID.randomUUID(),
                "John Doe",
                "john@example.com",
                LocalDateTime.of(2026, 4, 29, 22, 0),
                com.order.api01authgateway.dto.OrderStatus.PENDING,
                new BigDecimal("10.00"),
                List.of()
        );

        Mono<org.springframework.http.ResponseEntity<byte[]>> response =
                (Mono<org.springframework.http.ResponseEntity<byte[]>>) ReflectionTestUtils.invokeMethod(controller, "proxy", request, body);

        var entity = response.block();

        org.junit.jupiter.api.Assertions.assertNotNull(entity);
        org.junit.jupiter.api.Assertions.assertEquals(201, entity.getStatusCode().value());
        org.junit.jupiter.api.Assertions.assertEquals("internal-created", new String(entity.getBody()));

        var recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        org.junit.jupiter.api.Assertions.assertNotNull(recordedRequest);
        org.junit.jupiter.api.Assertions.assertEquals("POST", recordedRequest.getMethod());
        org.junit.jupiter.api.Assertions.assertEquals("/api/orders", recordedRequest.getPath());
        org.junit.jupiter.api.Assertions.assertEquals("Bearer token", recordedRequest.getHeader(HttpHeaders.AUTHORIZATION));
        org.junit.jupiter.api.Assertions.assertTrue(recordedRequest.getBody().readUtf8().contains("\"customerName\":\"John Doe\""));
    }

    private String validOrderJson() {
        return """
                {
                  "customerName":"John Doe",
                  "customerEmail":"john@example.com",
                  "orderDate":"2026-04-29T22:00:00",
                  "status":"PENDING",
                  "totalAmount":10.00,
                  "items":[]
                }
                """;
    }

    private String validOrderItemJson() {
        return """
                {
                  "productName":"Keyboard",
                  "quantity":1,
                  "unitPrice":10.00
                }
                """;
    }
}
