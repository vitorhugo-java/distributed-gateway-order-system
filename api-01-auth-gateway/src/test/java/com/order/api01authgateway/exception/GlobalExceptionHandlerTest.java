package com.order.api01authgateway.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <h1>GlobalExceptionHandlerTest</h1>
 * <p>Verifies HTTP mapping and payload details produced by {@link GlobalExceptionHandler}.</p>
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldMapBadCredentialsToUnauthorized() {
        ProblemDetail detail = handler.handleBadCredentials(new BadCredentialsException("Invalid credentials"));

        assertEquals(HttpStatus.UNAUTHORIZED.value(), detail.getStatus());
        assertEquals("Bad Credentials", detail.getTitle());
        assertEquals("Invalid credentials", detail.getDetail());
        assertEquals(URI.create("https://api.orders.com/errors/unauthorized"), detail.getType());
    }

    @Test
    void shouldMapAuthenticationExceptionToUnauthorized() {
        AuthenticationException ex = new AuthenticationException("Auth failed") { };

        ProblemDetail detail = handler.handleAuthenticationException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED.value(), detail.getStatus());
        assertEquals("Authentication Failed", detail.getTitle());
        assertEquals("Auth failed", detail.getDetail());
        assertEquals(URI.create("https://api.orders.com/errors/auth-failed"), detail.getType());
    }

    @Test
    void shouldMapUnexpectedExceptionToInternalServerError() {
        ProblemDetail detail = handler.handleGeneralException(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), detail.getStatus());
        assertEquals("Internal Server Error", detail.getTitle());
        assertEquals("An unexpected error occurred", detail.getDetail());
        assertEquals(URI.create("https://api.orders.com/errors/internal-error"), detail.getType());
    }
}
