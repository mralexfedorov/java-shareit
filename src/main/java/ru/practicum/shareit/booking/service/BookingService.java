package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto save(BookingDto bookingDto, int userId);

    BookingDto approve(int userId, int bookingId, boolean status);

    BookingDto getById(int bookingId, int userId);

    List<BookingDto> getByBooker(int bookerId, String state);

    List<BookingDto> getByOwner(int ownerId, String state);
}
