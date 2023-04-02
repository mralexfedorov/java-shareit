package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private int id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemDto item;
    private Integer itemId;

    private UserDto booker;

    private BookingStatus status;
}
