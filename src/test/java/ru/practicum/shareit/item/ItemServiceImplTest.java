package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;

    @Test
    void itemServiceTest() {
        // item creating
        UserDto userDto = new UserDto(1, "Vlad", "vlad@email.com");

        // when
        UserDto createdUser = userService.createUser(userDto);

        ItemDto itemDto = new ItemDto(1, "Thing 1", "Thing 1 for doing something", true,
                createdUser, 1, null, null, null);

        ItemDto createdItem = itemService.addItem(createdUser.getId(), itemDto);

        // then
        ItemDto itemDtoForCheck = itemService.getItemById(createdUser.getId(), createdItem.getId());
        assertThat(itemDtoForCheck.getId(), equalTo(createdItem.getId()));
        assertThat(itemDtoForCheck.getName(), equalTo(createdItem.getName()));
        assertThat(itemDtoForCheck.getDescription(), equalTo(createdItem.getDescription()));

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", createdItem.getId())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(createdItem.getName()));
        assertThat(item.getDescription(), equalTo(createdItem.getDescription()));

        // item updating
        itemDto.setName("Thing 2 for doing something");

        // when
        itemService.updateItem(createdItem.getId(), createdItem, createdItem.getId());

        // then
        query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        item = query.setParameter("id", createdItem.getId())
                .getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(createdItem.getName()));
        assertThat(item.getDescription(), equalTo(createdItem.getDescription()));

        List<ItemDto> userItems = itemService.getAllItemsByOwnerId(userDto.getId(),
                PageRequest.of(0, 20));

        assertThat(userItems.size(), equalTo(1));
        assertThat(userItems.get(0).getName(), equalTo(createdItem.getName()));
        assertThat(userItems.get(0).getDescription(), equalTo(createdItem.getDescription()));
    }

    @Test
    void itemValidationTest() {
        // item creating
        UserDto userDto = new UserDto(1, "Vlad", "vlad@email.com");

        // when
        ItemDto itemDto = new ItemDto(1, null, null, null,
                userDto, 1, null, null, null);

        // then
        try {
            itemService.addItem(userDto.getId(), itemDto);
        } catch (ValidationException e) {
            //then
            assertThat(e.getMessage(), equalTo("Предмет с id " +  itemDto.getId() + " не доступен"));
        }

        // when
        itemDto.setAvailable(true);

        // then
        try {
            itemService.addItem(userDto.getId(), itemDto);
        } catch (ValidationException e) {
            //then
            assertThat(e.getMessage(), equalTo("У предмета с id " +  itemDto.getId()
                    + " не заполнено описание"));
        }

        // when
        itemDto.setDescription("Thing 1 for doing something");

        // then
        try {
            itemService.addItem(userDto.getId(), itemDto);
        } catch (ValidationException e) {
            //then
            assertThat(e.getMessage(), equalTo("У предмета с id " +  itemDto.getId()
                    + " не заполнено название"));
        }

        // when
        itemDto.setName("Thing 1");

        // then
        try {
            itemService.addItem(userDto.getId(), itemDto);
        } catch (NoSuchElementException e) {
            //then
            assertThat(e.getMessage(), equalTo("Пользователь с таким id " + userDto.getId()
                    + " не существует"));
        }

        try {
            itemService.getItemById(userDto.getId(), itemDto.getId());
        } catch (NoSuchElementException e) {
            //then
            assertThat(e.getMessage(), equalTo("Предмет с таким id " + itemDto.getId()
                    + " не существует"));
        }


    }
}
