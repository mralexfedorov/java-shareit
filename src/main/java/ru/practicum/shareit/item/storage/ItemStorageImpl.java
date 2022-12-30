package ru.practicum.shareit.item.storage;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
@AllArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private final ItemDao itemDao;

    @Override
    public Item addItem(int ownerId, Item item) {
        return itemDao.addItem(item);
    }

    @Override
    public Item updateItem(int ownerId, Item item, int id) {
        return itemDao.updateItem(item, id);
    }

    @Override
    public Item getItemById(int id) {
        return itemDao.getItemById(id);
    }

    @Override
    public List<Item> getAllItemsByOwnerId(int ownerId) {
        return itemDao.getAllItemsByOwnerId(ownerId);
    }

    @Override
    public List<Item> searchAvailableItemsByName(String name) {
        return itemDao.searchAvailableItemsByName(name);
    }
}
