package ru.practicum.shareit.item.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@AllArgsConstructor
public class ItemStorageImpl implements ItemStorage{
    private final ItemDao itemDao;
    private final UserDao userDao;

    @Override
    public ItemDto addItem(int ownerId, ItemDto item) {
        User userFromStorage = userDao.getUserById(ownerId);
        if (userFromStorage == null) {
            throw new UserNotFoundException(
                    String.format("Пользователь с таким id %s не существует", ownerId));
        }
        if (item.getAvailable() == null) {
            throw new ItemWithoutAvailableException(
                    String.format("Предмет с id %s не доступен", item.getId()));
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ItemDescriptionEmptyException(
                    String.format("У предмета с id %s не заполнено описание", item.getId()));
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ItemNameEmptyException(
                    String.format("У предмета с id %s не заполнено название", item.getId()));
        }
        ItemDto createdItem = ItemMapper.toItemDto(itemDao.addItem(ItemMapper.toItem(ownerId, item)));
        log.debug("Пользователь {} создан.", createdItem.getName());
        return createdItem;
    }

    @Override
    public ItemDto updateItem(int ownerId, ItemDto item, int id) {
        User userFromStorage = userDao.getUserById(ownerId);
        if (userFromStorage == null) {
            throw new UserNotFoundException(
                    String.format("Пользователь с таким id %s не существует", id));
        }
        Item itemFromStorage = itemDao.getItemById(id);
        if (itemFromStorage == null || itemFromStorage.getOwnerId() != ownerId) {
            throw new ItemNotFoundException(
                    String.format("Предмет с таким id %s не существует", id));
        }

        item.setId(id);
        ItemDto updatedItem = ItemMapper.toItemDto(itemDao.updateItem(ItemMapper.toExistsItem(item,
                itemFromStorage), id));
        log.debug("Данные о предмете {} обновлены.", updatedItem.getName());
        return updatedItem;
    }

    @Override
    public ItemDto getItemById(int id) {
        Item itemFromStorage = itemDao.getItemById(id);
        if (itemFromStorage == null) {
            throw new UserNotFoundException(
                    String.format("Предмет с таким id %s не существует", id));
        }

        return ItemMapper.toItemDto(itemFromStorage);
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(int ownerId) {
        return itemDao.getAllItemsByOwnerId(ownerId).stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAvailableItemsByName(String name) {
        return itemDao.getAvailableItemsByName(name).stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
