package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class UserEmailDuplicatedException extends RuntimeException {
    public UserEmailDuplicatedException(final String message) {
        super(message);
    }
}
