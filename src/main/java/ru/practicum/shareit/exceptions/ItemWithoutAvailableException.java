package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ItemWithoutAvailableException extends RuntimeException {
    public ItemWithoutAvailableException(final String message) {
        super(message);
    }
}
