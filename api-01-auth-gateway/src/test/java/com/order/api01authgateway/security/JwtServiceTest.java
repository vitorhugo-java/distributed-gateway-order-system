package com.order.api01authgateway.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <h1>JwtServiceTest</h1>
 * <p>Validates token generation, extraction, and validation behavior for {@link JwtService}.</p>
 */
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);
    }

    @Test
    void shouldGenerateToken() {
        UserDetails userDetails = new User("test@email.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
    }

    @Test
    void shouldExtractUsername() {
        UserDetails userDetails = new User("test@email.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);
        assertEquals("test@email.com", username);
    }

    @Test
    void shouldValidateToken() {
        UserDetails userDetails = new User("test@email.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldReturnFalseWhenTokenBelongsToDifferentUser() {
        UserDetails tokenOwner = new User("owner@email.com", "password", Collections.emptyList());
        UserDetails otherUser = new User("other@email.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(tokenOwner);

        assertFalse(jwtService.isTokenValid(token, otherUser));
    }

    @Test
    void shouldReturnFalseWhenTokenIsExpired() {
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", -1000L);
        UserDetails userDetails = new User("test@email.com", "password", Collections.emptyList());
        String token = jwtService.generateToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldThrowWhenTokenIsMalformed() {
        assertThrows(JwtException.class, () -> jwtService.extractUsername("malformed.token.value"));
    }
}
