package com.order.api02orderscrud.exception;

import com.order.api02orderscrud.dto.OrderDTO;
import com.order.api02orderscrud.entity.Order;
import com.order.api02orderscrud.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.data.util.TypeInformation;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <h1>GlobalExceptionHandlerTest</h1>
 * <p>Validates HTTP status and response payload mapping for {@link GlobalExceptionHandler}.</p>
 */
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldMapEntityNotFoundExceptionTo404() {
        ResponseEntity<String> response = handler.handleEntityNotFoundException(new EntityNotFoundException("Order not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Order not found", response.getBody());
    }

    @Test
    void shouldMapValidationExceptionTo400WithFieldErrors() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "orderDTO");
        bindingResult.addError(new FieldError("orderDTO", "customerName", "must not be blank"));

        MethodParameter parameter = new MethodParameter(
                TestTarget.class.getDeclaredMethod("accept", OrderDTO.class),
                0
        );
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("must not be blank", response.getBody().get("customerName"));
    }

    @Test
    void shouldMapRuntimeExceptionTo500() {
        ResponseEntity<String> response = handler.handleRuntimeException(new RuntimeException("Unexpected"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Unexpected", response.getBody());
    }

    @Test
    void shouldMapPropertyReferenceExceptionTo400() {
        PropertyReferenceException ex = new PropertyReferenceException("[\"string\"]", TypeInformation.of(Order.class), java.util.Collections.emptyList());

        ResponseEntity<String> response = handler.handlePropertyReferenceException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ex.getMessage(), response.getBody());
    }

    static class TestTarget {
        void accept(OrderDTO dto) {
            OrderDTO ignored = new OrderDTO(null, dto.customerName(), dto.customerEmail(), LocalDateTime.now(), OrderStatus.PENDING, BigDecimal.ZERO, java.util.List.of());
            ignored.hashCode();
        }
    }
}
