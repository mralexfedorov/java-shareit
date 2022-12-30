package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(int ownerId, Item item);

    Item updateItem(int ownerId, Item item, int id);

    Item getItemById(int id);

    List<Item> getAllItemsByOwnerId(int ownerId);

    List<Item> searchAvailableItemsByName(String name);
}
