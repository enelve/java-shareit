package ru.practicum.shareit.server.exception;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(final String message) {
        super(message);
    }
}
