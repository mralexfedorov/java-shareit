package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private UserDto userDto1;
    private UserDto userDto2;
    private ItemDto itemDto;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        userDto1 = new UserDto(
                0,
                "John",
                "john.doe@mail.com");
        userDto2 = new UserDto(
                0,
                "Bob",
                "bob.doe@mail.com");
        itemDto = new ItemDto(
                1,
                "thing 1",
                "thing 1",
                true,
                userDto1,
                0,
                null,
                null,
                null
        );
        bookingDto = new BookingDto(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                itemDto,
                itemDto.getId(),
                userDto2,
                BookingStatus.WAITING
        );
    }

    @Test
    void createBookingAndCheck() throws Exception {
        UserDto createdUser1 = mapper.readValue(mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())))
                .andReturn().getResponse().getContentAsString(), UserDto.class);

        UserDto createdUser2 = mapper.readValue(mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto2.getName())))
                .andExpect(jsonPath("$.email", is(userDto2.getEmail())))
                .andReturn().getResponse().getContentAsString(), UserDto.class);

        itemDto.setOwner(createdUser1);
        bookingDto.setBooker(createdUser2);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", createdUser1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .header("X-Sharer-User-Id", createdUser2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(itemDto.getId())));
    }
}
