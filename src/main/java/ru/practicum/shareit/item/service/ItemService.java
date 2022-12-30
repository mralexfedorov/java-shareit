package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(int ownerId, ItemDto itemDto);

    ItemDto updateItem(int ownerId, ItemDto itemDto, int id);

    ItemDto getItemById(int id);

    List<ItemDto> getAllItemsByOwnerId(int ownerId);

    List<ItemDto> searchAvailableItemsByName(String name);
}
