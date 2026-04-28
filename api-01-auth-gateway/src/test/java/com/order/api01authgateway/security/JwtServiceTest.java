package com.order.api01authgateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

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
}
