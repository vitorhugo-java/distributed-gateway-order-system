package com.order.api01authgateway.service;

import com.order.api01authgateway.dto.LoginRequest;
import com.order.api01authgateway.dto.TokenResponse;
import com.order.api01authgateway.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldAuthenticateSuccessfully() {
        LoginRequest request = new LoginRequest("test@email.com", "password");
        UserDetails userDetails = new User("test@email.com", "password", Collections.emptyList());

        when(userDetailsService.loadUserByUsername(request.email())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mocked-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        TokenResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("mocked-token", response.token());
        assertEquals("Bearer", response.type());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
