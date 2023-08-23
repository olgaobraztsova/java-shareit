package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AvailabilityException extends ResponseStatusException {
    public AvailabilityException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
