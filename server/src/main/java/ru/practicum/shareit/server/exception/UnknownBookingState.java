package ru.practicum.shareit.server.exception;

public class UnknownBookingState extends RuntimeException {
    public UnknownBookingState(final String message) {
        super(message);
    }
}

