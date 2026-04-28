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

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public TokenResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails user = userDetailsService.loadUserByUsername(request.email());
        String jwtToken = jwtService.generateToken(user);

        return new TokenResponse(jwtToken, "Bearer", jwtService.getExpirationTime());
    }
}
