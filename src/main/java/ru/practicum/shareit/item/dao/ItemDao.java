package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    Item addItem(Item item);

    Item updateItem(Item item, int id);

    Item getItemById(int id);

    List<Item> getAllItemsByOwnerId(int ownerId);

    List<Item> searchAvailableItemsByName(String name);
}
