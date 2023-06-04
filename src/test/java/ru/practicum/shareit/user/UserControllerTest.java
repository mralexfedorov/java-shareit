package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private UserDto userDto1;
    private UserDto userDto2;

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
    }

    @Test
    void createUserAndCheck() throws Exception {
        UserDto createdUser1 = mapper.readValue(mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())))
                .andReturn().getResponse().getContentAsString(), UserDto.class);

        mvc.perform(get("/users/" + createdUser1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(createdUser1.getName())))
                .andExpect(jsonPath("$.email", is(createdUser1.getEmail())));

        mvc.perform(get("/users"));

        UserDto createdUser2 = mapper.readValue(mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(userDto2.getName())))
                .andExpect(jsonPath("$.email", is(userDto2.getEmail())))
                .andReturn().getResponse().getContentAsString(), UserDto.class);

        mvc.perform(get("/users"));

        createdUser2.setEmail("bob.doe@mail.org");

        mvc.perform(patch("/users/" + createdUser2.getId())
                        .content(mapper.writeValueAsString(createdUser2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(createdUser2.getName())))
                .andExpect(jsonPath("$.email", is(createdUser2.getEmail())));

        mvc.perform(get("/users"));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(createdUser1.getName())))
                .andExpect(jsonPath("$[0].email", is(createdUser1.getEmail())))
                .andExpect(jsonPath("$[1].name", is(createdUser2.getName())))
                .andExpect(jsonPath("$[1].email", is(createdUser2.getEmail())));

        mvc.perform(delete("/users/" + createdUser1.getId()));
        mvc.perform(delete("/users/" + createdUser2.getId()));
    }
}
