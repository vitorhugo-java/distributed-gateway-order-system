package com.order.api01authgateway.service;

import com.order.api01authgateway.dto.LoginRequest;
import com.order.api01authgateway.dto.TokenResponse;
import com.order.api01authgateway.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Service component responsible for handling user authentication business logic.
 * <p>
 * This service coordinates the authentication process by verifying credentials against
 * configured identity providers and generating secure access tokens using {@link JwtService}.
 * It relies on Spring Security's {@link AuthenticationManager} to perform BCrypt-based
 * password verification.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Authenticates a user and returns a token response.
     *
     * @param request The login request containing username and password.
     * @return A {@link TokenResponse} containing the generated JWT.
     * @throws org.springframework.security.core.AuthenticationException If authentication fails (e.g., bad credentials).
     */
    public TokenResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails user = userDetailsService.loadUserByUsername(request.username());
        String jwtToken = jwtService.generateToken(user);

        return new TokenResponse(jwtToken, "Bearer", jwtService.getExpirationTime());
    }
}
