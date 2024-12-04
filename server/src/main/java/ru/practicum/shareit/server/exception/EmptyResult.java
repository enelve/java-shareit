package ru.practicum.shareit.server.exception;

public class EmptyResult extends RuntimeException {
    public EmptyResult(final String message) {
        super(message);
    }
}