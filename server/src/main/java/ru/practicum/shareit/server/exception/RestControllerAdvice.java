package ru.practicum.shareit.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@ControllerAdvice
public class RestControllerAdvice {

    private record Error(String error, String description) {
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Client request error: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Set<String>> handleException(NotFoundException e) {
        return processException(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidEmailException.class})
    public ResponseEntity<Set<String>> handleException(InvalidEmailException e) {
        return processException(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ItemNotValidException.class})
    public ResponseEntity<Error> handleException(ItemNotValidException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new Error("ItemNotValidException", e.getMessage()));
    }

    @ExceptionHandler({UnknownBookingState.class})
    public ResponseEntity<Set<String>> handleException(UnknownBookingState e) {
        return processException(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({EmptyResult.class})
    public ResponseEntity<Set<String>> handleException(EmptyResult e) {
        return processException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({DuplicateEmailException.class})
    public ResponseEntity<Set<String>> handleException(DuplicateEmailException e) {
        return processException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ItemOwnerMismatchException.class)
    public ResponseEntity<Set<String>> handleException(ItemOwnerMismatchException e) {
        return processException(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    private ResponseEntity<Set<String>> processException(String message, HttpStatus httpStatus) {
        log.error("Client request error: {}", message);
        return ResponseEntity
                .status(httpStatus)
                .body(Set.of(message));
    }
}
