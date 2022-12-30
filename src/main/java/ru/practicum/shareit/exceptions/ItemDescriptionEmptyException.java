package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class ItemDescriptionEmptyException extends RuntimeException {
    public ItemDescriptionEmptyException(final String message) {
        super(message);
    }
}
