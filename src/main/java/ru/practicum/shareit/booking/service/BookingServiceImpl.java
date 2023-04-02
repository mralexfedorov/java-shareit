package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public BookingDto save(BookingDto bookingDto, int userId) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(
                String.format("Пользователь с таким id %s не существует", userId)));

        Integer itemId = bookingDto.getItemId();
        if (itemId == null) {
            throw new NoSuchElementException("Предмет не указан");
        }

        Item itemFromStorage = itemRepository.findById(itemId).orElseThrow(()
                -> new NoSuchElementException(String.format("Предмет с таким id %s не существует", itemId)));
        if (!itemFromStorage.isAvailable()) {
            throw new ValidationException("Предмет не доступен для бронирования");
        }

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null
                || bookingDto.getStart().isBefore(LocalDateTime.now())
                || bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Период бронирования указан некорректно");
        }

        bookingDto.setItem(itemFromStorage);
        bookingDto.setBooker(booker);
        bookingDto.setStatus(BookingStatus.WAITING);

        if (bookingDto.getItem().getOwner().getId() == userId) {
            throw new NoSuchElementException(String.format("Пользователь с id %s является владельцем предмета",
                    userId));
        }

        BookingDto createdBooking = BookingMapper.toBookingDto(
                bookingRepository.save(BookingMapper.toBooking(bookingDto)));
        log.debug("Создано бронирование {} предмета {} пользователем с id={}",
                createdBooking.getId(),
                createdBooking.getItem().getName(),
                createdBooking.getBooker().getId());
        return createdBooking;
    }

    @Transactional
    @Override
    public BookingDto approve(int userId, int bookingId, boolean status) {

        BookingDto bookingDto = BookingMapper.toBookingDto(bookingRepository.findById(bookingId).orElseThrow(()
                -> new NoSuchElementException(String.format("Бронирование с таким id %s не существует", bookingId))));

        Integer itemId = bookingDto.getItemId();
        if (itemId == null) {
            throw new NoSuchElementException("Предмет не указан");
        }

        Item itemFromStorage = itemRepository.findById(itemId).orElseThrow(()
                -> new NoSuchElementException(String.format("Предмет с таким id %s не существует", itemId)));
        if (!itemFromStorage.isAvailable()) {
            throw new ValidationException("Предмет не доступен для бронирования");
        }

        if (bookingDto.getStart().isBefore(LocalDateTime.now().with(LocalTime.MIN))
                || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Период бронирования указан некорректно");
        }

        if (bookingDto.getItem().getOwner().getId() != userId) {
            throw new NoSuchElementException(String.format("Пользователь с id %s не является владельцем предмета",
                    userId));
        }

        if (bookingDto.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Статус заявки бронирования изменить нельзя");
        }

        BookingStatus bookingStatus = status ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        bookingDto.setStatus(bookingStatus);

        return BookingMapper.toBookingDto(
                bookingRepository.save(BookingMapper.toBooking(bookingDto)));
    }

    @Override
    public BookingDto getById(int bookingId, int userId) {
        userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(
                String.format("Пользователь с таким id %s не существует", userId)));

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new NoSuchElementException(
                String.format("Бронирование с таким id %s не существует", bookingId)));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NoSuchElementException(String.format("Пользователь с id %s не является ни автором бронирования, " +
                            "ни владельцем предмета",
                    userId));
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getByBooker(int bookerId, String state, Pageable pageable) {
        userRepository.findById(bookerId).orElseThrow(() -> new NoSuchElementException(
                String.format("Пользователь с таким id %s не существует", bookerId)));

        BookingState bookingState = BookingState.ALL;
        LocalDateTime now = LocalDateTime.now();

        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }

        switch (bookingState) {
            case ALL: return bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageable).stream()
                    .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case CURRENT: return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId,
                    now, now, pageable).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case PAST: return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId,
                    now, pageable).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case FUTURE: return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(bookerId,
                    now, pageable).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default: return bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.valueOf(state),
                            pageable).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingDto> getByOwner(int ownerId, String state, Pageable pageable) {
        userRepository.findById(ownerId).orElseThrow(() -> new NoSuchElementException(
                String.format("Пользователь с таким id %s не существует", ownerId)));

        BookingState bookingState = BookingState.ALL;
        LocalDateTime now = LocalDateTime.now();

        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }

        switch (bookingState) {
            case ALL: return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageable).stream()
                    .map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case CURRENT: return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId,
                    now, now, pageable).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case PAST: return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId,
                    now, pageable).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            case FUTURE: return bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId,
                    now, pageable).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
            default: return bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.valueOf(state),
                            pageable).stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
        }
    }
}
