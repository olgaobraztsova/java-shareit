package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserInputMissingOrInvalidEmailException extends ResponseStatusException {
    public UserInputMissingOrInvalidEmailException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}