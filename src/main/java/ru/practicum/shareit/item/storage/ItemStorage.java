package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {
    ItemDto addItem(int ownerId, ItemDto item);

    ItemDto updateItem(int ownerId, ItemDto item, int id);

    ItemDto getItemById(int id);

    List<ItemDto> getAllItemsByOwnerId(int ownerId);

    List<ItemDto> getAvailableItemsByName(String name);
}
