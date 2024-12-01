package ru.practicum.shareit.exception;

public class EmptyResult extends RuntimeException {
    public EmptyResult(final String message) {
        super(message);
    }
}