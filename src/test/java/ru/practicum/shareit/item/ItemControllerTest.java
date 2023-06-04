package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                0,
                "John",
                "john.doe@mail.com");

        itemDto = new ItemDto(
                0,
                "thing 1",
                "thing 1",
                false,
                userDto,
                0,
                null,
                null,
                null
        );
    }

    @Test
    void createItemAndCheck() throws Exception {
        UserDto createdUser = mapper.readValue(mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andReturn().getResponse().getContentAsString(), UserDto.class);

        itemDto.setOwner(createdUser);

        ItemDto createdItemDto = mapper.readValue(mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", createdUser.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andReturn().getResponse().getContentAsString(), ItemDto.class);

        mvc.perform(get("/items/" + createdItemDto.getId()).header("X-Sharer-User-Id", createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(createdItemDto.getName())))
                .andExpect(jsonPath("$.description", is(createdItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(createdItemDto.getAvailable())));

        createdItemDto.setAvailable(true);

        mvc.perform(patch("/items/" + createdItemDto.getId())
                        .content(mapper.writeValueAsString(createdItemDto))
                        .header("X-Sharer-User-Id", createdUser.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(createdItemDto.getName())))
                .andExpect(jsonPath("$.description", is(createdItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(createdItemDto.getAvailable())));
    }
}
