package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingDto save(@RequestBody BookingDto bookingDto,
                           @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Создание бронирования {} пользоватлем с id = {}", bookingDto, userId);
        return bookingService.save(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable int bookingId,
                              @RequestParam Boolean approved,
                              @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Изменение статуса бронирования {} пользователем с id = {}", bookingId, userId);
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable int bookingId,
                              @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение бронирования {} пользователем c id = {}", bookingId, userId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getByBooker(@RequestHeader("X-Sharer-User-Id") int userId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получение бронирований со статусом {} пользователем c id = {}", state, userId);
        return bookingService.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(@RequestHeader("X-Sharer-User-Id") int owner,
                                       @RequestParam(defaultValue = "ALL") String state) {
        log.info("Получение бронирований со статусом {} владельца c id = {}", state, owner);
        return bookingService.getByOwner(owner, state);
    }
}
