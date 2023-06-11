package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.config.PersistenceConfig;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@DataJpaTest
@Import(PersistenceConfig.class)
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyRepositoryByPersistingBooking() {
        User user = new User(1, "Vlad", "vlad@email.com");

        User cretedUser = userRepository.save(user);
        Assertions.assertNotNull(cretedUser.getId());

        Item item = new Item(1, "Thing 1", "Thing 1 for doing something", true,
                cretedUser, 1);

        Item createdItem = itemRepository.save(item);
        Assertions.assertNotNull(createdItem.getId());

        Booking booking = new Booking(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                createdItem,
                cretedUser,
                BookingStatus.WAITING
        );

        Booking createdBooking = bookingRepository.save(booking);
        Assertions.assertNotNull(createdBooking.getId());
    }
}
