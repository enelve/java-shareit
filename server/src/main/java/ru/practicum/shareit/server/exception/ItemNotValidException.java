package ru.practicum.shareit.server.exception;

public class ItemNotValidException extends RuntimeException {
    public ItemNotValidException(final String message) {
        super(message);
    }
}
