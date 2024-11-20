package ru.practicum.shareit.exception;

public class ItemNotValidException extends RuntimeException {
    public ItemNotValidException(final String message) {
        super(message);
    }
}
