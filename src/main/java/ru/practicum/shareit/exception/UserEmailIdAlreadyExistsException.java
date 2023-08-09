package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserEmailIdAlreadyExistsException extends ResponseStatusException {
    public UserEmailIdAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
