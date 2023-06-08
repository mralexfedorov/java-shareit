package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    private final EntityManager em;
    private final UserService service;

    @Test
    void userServiceTest() {
        // user creating
        UserDto userDto = new UserDto(1, "Vlad", null);

        // when
        try {
            service.createUser(userDto);
        } catch (ValidationException e) {
            //then
            assertThat(e.getMessage(), equalTo("Email не может быть пустым."));
        }

        // when
        userDto.setEmail("vlad@email.com");
        service.createUser(userDto);

        // then
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        userDto.setId(user.getId());

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

        // user updating
        userDto.setEmail("vlad@mail.com");

        // when
        service.updateUser(userDto, userDto.getId());

        // then
        query = em.createQuery("Select u from User u where u.email = :email", User.class);
        user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

        // user updating, email is not valid
        userDto.setEmail("vlademail.com");

        // when
        try {
            service.updateUser(userDto, userDto.getId());
        } catch (ValidationException e) {
            //then
            assertThat(e.getMessage(), equalTo("Невалидный Email"));
        }

        // user deleting
        service.deleteUser(userDto.getId());

        // when
        try {
            service.getUser(userDto.getId());
        } catch (NoSuchElementException e) {
            //then
            assertThat(e.getMessage(), equalTo("Пользователь с таким id " + userDto.getId()
                    + " не существует"));
        }

        List<UserDto> users = service.findAllUsers();
        assertThat(users.size(), equalTo(0));
    }
}
