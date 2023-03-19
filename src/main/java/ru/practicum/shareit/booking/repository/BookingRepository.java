package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByBookerIdOrderByStartDesc(int bookerId);

    List<Booking> findAllByBookerIdAndStatus(int bookerId, BookingStatus bookingStatus);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(int bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(int bookerId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(int bookerId, LocalDateTime nowForStart,
                                                                             LocalDateTime nowForEnd);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(int ownerId);

    List<Booking> findAllByItemOwnerIdAndStatus(int ownerId, BookingStatus bookingStatus);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(int ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(int ownerId, LocalDateTime now);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(int ownerId, LocalDateTime nowForStart,
                                                                                LocalDateTime nowForEnd);

    List<Booking> findAllByItemIdAndItemOwnerIdAndStartBeforeOrderByStartDesc(int itemId, int ownerId,
                                                                              LocalDateTime now);

    List<Booking> findAllByItemIdAndItemOwnerIdAndStatusAndStartAfterOrderByStartDesc(int itemId,
                                                                                      int ownerId,
                                                                                      BookingStatus bookingStatus,
                                                                                      LocalDateTime now);
}
