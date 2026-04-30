package com.order.api01authgateway.security;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * <h1>JwtAuthenticationFilterTest</h1>
 * <p>Validates authentication and bypass branches of {@link JwtAuthenticationFilter}.</p>
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSkipWhenAuthorizationHeaderIsMissing() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extractUsername(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldFilterAsyncDispatches() {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        org.junit.jupiter.api.Assertions.assertFalse(filter.shouldNotFilterAsyncDispatch());
    }

    @Test
    void shouldSkipWhenAuthorizationHeaderIsNotBearer() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtService, never()).extractUsername(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldAuthenticateWhenTokenIsValid() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        UserDetails userDetails = new User("test@email.com", "password", Collections.emptyList());

        when(jwtService.extractUsername("valid-token")).thenReturn("test@email.com");
        when(userDetailsService.loadUserByUsername("test@email.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid-token", userDetails)).thenReturn(true);

        filter.doFilter(request, response, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService).loadUserByUsername("test@email.com");
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        UserDetails userDetails = new User("test@email.com", "password", Collections.emptyList());

        when(jwtService.extractUsername("invalid-token")).thenReturn("test@email.com");
        when(userDetailsService.loadUserByUsername("test@email.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("invalid-token", userDetails)).thenReturn(false);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldNotReloadUserWhenContextAlreadyAuthenticated() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("existing", null, Collections.emptyList())
        );
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtService.extractUsername("valid-token")).thenReturn("test@email.com");

        filter.doFilter(request, response, chain);

        verify(userDetailsService, never()).loadUserByUsername(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMalformed() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer malformed");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtService.extractUsername("malformed")).thenThrow(new IllegalArgumentException("Malformed token"));

        filter.doFilter(request, response, chain);

        assertEquals(401, response.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldSkipAuthenticationWhenTokenHasNoUsername() throws ServletException, IOException {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(jwtService.extractUsername("valid-token")).thenReturn(null);

        filter.doFilter(request, response, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService, never()).loadUserByUsername(org.mockito.ArgumentMatchers.anyString());
    }
}
