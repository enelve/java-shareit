package ru.practicum.shareit.server.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class RestControllerAdviceTest {
    private RestControllerAdvice restControllerAdvice;

    @BeforeEach
    void init() {
        restControllerAdvice = new RestControllerAdvice();
    }

    @Test
    void handleNotFoundException() {
        ResponseEntity<Set<String>> response = restControllerAdvice.handleException(new NotFoundException("test"));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleInvalidEmailException() {
        ResponseEntity<Set<String>> response = restControllerAdvice.handleException(new InvalidEmailException("test"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleItemNotValidException() {
        var response = restControllerAdvice.handleException(new ItemNotValidException("test"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleUnknownBookingStateException() {
        ResponseEntity<Set<String>> response = restControllerAdvice.handleException(new UnknownBookingState("test"));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleEmptyResultException() {
        ResponseEntity<Set<String>> response = restControllerAdvice.handleException(new EmptyResult("test"));
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void handleDuplicateEmailException() {
        ResponseEntity<Set<String>> response = restControllerAdvice.handleException(new DuplicateEmailException("test"));
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
