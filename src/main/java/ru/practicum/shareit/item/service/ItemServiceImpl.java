package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(int ownerId, ItemDto itemDto) {
        User userFromStorage = userStorage.getUserById(ownerId);
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
        ItemDto createdItem = ItemMapper.toItemDto(itemStorage.addItem(ownerId, ItemMapper.toItem(ownerId, itemDto)));
        log.debug("Создан предмет {}.", createdItem.getName());
        return createdItem;
    }

    @Override
    public ItemDto updateItem(int ownerId, ItemDto itemDto, int id) {
        if (userStorage.getUserById(ownerId) == null) {
            throw new UserNotFoundException(
                    String.format("Пользователь с таким id %s не существует", id));
        }
        Item itemFromStorage = itemStorage.getItemById(id);
        if (itemFromStorage == null || itemFromStorage.getOwnerId() != ownerId) {
            throw new ItemNotFoundException(
                    String.format("Предмет с таким id %s не существует", id));
        }

        itemDto.setId(id);
        ItemDto updatedItem = ItemMapper.toItemDto(itemStorage.updateItem(ownerId,
                ItemMapper.toExistsItem(itemDto, itemFromStorage), id));

        log.debug("Данные о предмете {} обновлены.", updatedItem.getName());
        return updatedItem;
    }

    @Override
    public ItemDto getItemById(int id) {
        Item itemFromStorage = itemStorage.getItemById(id);
        if (itemFromStorage == null) {
            throw new UserNotFoundException(
                    String.format("Предмет с таким id %s не существует", id));
        }
        return ItemMapper.toItemDto(itemFromStorage);
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(int ownerId) {
        return itemStorage.getAllItemsByOwnerId(ownerId).stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItemsByName(String name) {
        return itemStorage.searchAvailableItemsByName(name).stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
