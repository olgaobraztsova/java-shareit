package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ConflictExistsException extends ResponseStatusException {
    public ConflictExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
