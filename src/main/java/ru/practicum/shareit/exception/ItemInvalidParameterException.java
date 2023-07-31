package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ItemInvalidParameterException extends ResponseStatusException {
    public ItemInvalidParameterException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
