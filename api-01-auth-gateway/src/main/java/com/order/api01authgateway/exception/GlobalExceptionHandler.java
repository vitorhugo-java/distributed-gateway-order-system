package com.order.api01authgateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

/**
 * Centralized exception handling component using {@link RestControllerAdvice}.
 * <p>
 * Maps application-specific exceptions to {@link ProblemDetail} objects compliant with
 * <a href="https://datatracker.ietf.org/doc/html/rfc7807">RFC 7807</a> (Problem Details for HTTP APIs).
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles {@link BadCredentialsException} and maps it to a 401 Unauthorized response.
     *
     * @param ex The exception encountered.
     * @return A {@link ProblemDetail} describing the error.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Bad Credentials");
        problemDetail.setType(URI.create("https://api.orders.com/errors/unauthorized"));
        return problemDetail;
    }

    /**
     * Handles general {@link AuthenticationException} and maps it to a 401 Unauthorized response.
     *
     * @param ex The exception encountered.
     * @return A {@link ProblemDetail} describing the error.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Authentication Failed");
        problemDetail.setType(URI.create("https://api.orders.com/errors/auth-failed"));
        return problemDetail;
    }

    /**
     * Handles unexpected {@link Exception} occurrences and maps them to a 500 Internal Server Error response.
     *
     * @param ex The exception encountered.
     * @return A {@link ProblemDetail} describing the error.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setType(URI.create("https://api.orders.com/errors/internal-error"));
        return problemDetail;
    }
}
