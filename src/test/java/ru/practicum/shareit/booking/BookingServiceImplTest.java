package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    private final EntityManager em;
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void bookingServiceTest() {
        // booking creating
        UserDto userDto1 = new UserDto(0, "Vlad", "vlady@email.com");
        UserDto userDto2 = new UserDto(0, "Bob", "joey.doe@mail.com");

        // when
        UserDto createdUser1 = userService.createUser(userDto1);
        UserDto createdUser2 = userService.createUser(userDto2);

        ItemDto itemDto = new ItemDto(1, "Thing 1", "Thing 1 for doing something", true,
                createdUser1, 1, null, null, null);

        ItemDto createdItem = itemService.addItem(createdUser1.getId(), itemDto);

        BookingDto bookingDto = new BookingDto(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                createdItem,
                createdItem.getId(),
                userDto2,
                BookingStatus.WAITING
        );

        BookingDto createdBooking = bookingService.save(bookingDto, createdUser2.getId());

        // then
        TypedQuery<Booking> query = em.createQuery("Select i from Booking i where i.id = :id",
                Booking.class);
        Booking booking = query.setParameter("id", createdBooking.getId())
                .getSingleResult();

        assertThat(booking.getId(), notNullValue());
    }

    @Test
    void bookingValidationTest() {
        // booking creating
        BookingDto bookingDto = new BookingDto(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                null,
                null,
                null,
                BookingStatus.WAITING
        );

        // then
        try {
            bookingService.save(bookingDto, 1);
        } catch (NoSuchElementException e) {
            //then
            assertThat(e.getMessage(), equalTo("Пользователь с таким id "
                    +  bookingDto.getId() + " не существует"));
        }
    }
}
