package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class ItemDaoImpl implements ItemDao {
    private HashMap<Integer, Item> items;
    private int id = 0;

    public ItemDaoImpl(HashMap<Integer, Item> items) {
        this.items = items;
    }

    @Override
    public Item addItem(Item item) {
        if (item.getId() == 0) {
            id++;
            item.setId(id);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item, int id) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(int id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAllItemsByOwnerId(int ownerId) {
        List<Item> itemsByOwnerId = new ArrayList<>();
        items.forEach((key, value) -> {
            if (value.getOwnerId() == ownerId) {
                itemsByOwnerId.add(value);
            }
        });

        return itemsByOwnerId;
    }

    @Override
    public List<Item> getAvailableItemsByName(String name) {
        List<Item> itemsByName = new ArrayList<>();
        if (!name.isEmpty()) {
            items.forEach((key, value) -> {
                if ((value.getName().toUpperCase().contains(name.toUpperCase()) ||
                        value.getDescription().toUpperCase().contains(name.toUpperCase())) && value.isAvailable()) {
                    itemsByName.add(value);
                }
            });
        }

        return itemsByName;
    }
}
