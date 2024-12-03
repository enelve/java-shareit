package ru.practicum.shareit.exception;

public class UnknownBookingState extends RuntimeException {
    public UnknownBookingState(final String message) {
        super(message);
    }
}

