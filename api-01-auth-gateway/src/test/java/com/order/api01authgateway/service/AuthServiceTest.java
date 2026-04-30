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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * <h1>AuthServiceTest</h1>
 * <p>Verifies successful authentication and failure paths for {@link AuthService}.</p>
 */
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
        LoginRequest request = new LoginRequest("test-user", "password");
        UserDetails userDetails = new User("test-user", "password", Collections.emptyList());

        when(userDetailsService.loadUserByUsername(request.username())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mocked-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        TokenResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("mocked-token", response.token());
        assertEquals("Bearer", response.type());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldThrowWhenCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest("test-user", "invalid");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(request));

        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtService, never()).generateToken(any(UserDetails.class));
    }
}
