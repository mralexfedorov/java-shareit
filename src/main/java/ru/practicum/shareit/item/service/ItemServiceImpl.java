package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(int ownerId, ItemDto itemDto) {
        UserDto userFromStorage = userStorage.getUserById(ownerId);
        if (userFromStorage == null) {
            throw new UserNotFoundException(
                    String.format("Пользователь с таким id %s не существует", ownerId));
        }
        if (itemDto.getAvailable() == null) {
            throw new ItemWithoutAvailableException(
                    String.format("Предмет с id %s не доступен", itemDto.getId()));
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ItemDescriptionEmptyException(
                    String.format("У предмета с id %s не заполнено описание", itemDto.getId()));
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ItemNameEmptyException(
                    String.format("У предмета с id %s не заполнено название", itemDto.getId()));
        }
        ItemDto createdItem = itemStorage.addItem(ownerId, itemDto);
        log.debug("Создан предмет {}.", createdItem.getName());
        return createdItem;
    }

    @Override
    public ItemDto updateItem(int ownerId, ItemDto itemDto, int id) {
        if (userStorage.getUserById(ownerId) == null) {
            throw new UserNotFoundException(
                    String.format("Пользователь с таким id %s не существует", id));
        }
        ItemDto itemFromStorage = itemStorage.getItemById(id);
        if (itemFromStorage == null || itemFromStorage.getOwnerId() != ownerId) {
            throw new ItemNotFoundException(
                    String.format("Предмет с таким id %s не существует", id));
        }

        itemDto.setId(id);
        ItemDto updatedItem = itemStorage.updateItem(ownerId, itemDto, id);
        log.debug("Данные о предмете {} обновлены.", updatedItem.getName());
        return updatedItem;
    }

    @Override
    public ItemDto getItemById(int id) {
        ItemDto itemFromStorage = itemStorage.getItemById(id);
        if (itemFromStorage == null) {
            throw new UserNotFoundException(
                    String.format("Предмет с таким id %s не существует", id));
        }
        return itemFromStorage;
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(int ownerId) {
        return itemStorage.getAllItemsByOwnerId(ownerId);
    }

    @Override
    public List<ItemDto> searchAvailableItemsByName(String name) {
        return itemStorage.searchAvailableItemsByName(name);
    }
}
