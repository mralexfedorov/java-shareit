package ru.practicum.shareit.item.storage;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private final ItemDao itemDao;

    @Override
    public ItemDto addItem(int ownerId, ItemDto item) {
        return ItemMapper.toItemDto(itemDao.addItem(ItemMapper.toItem(ownerId, item)));
    }

    @Override
    public ItemDto updateItem(int ownerId, ItemDto item, int id) {
        Item itemFromStorage = itemDao.getItemById(id);

        return ItemMapper.toItemDto(itemDao.updateItem(ItemMapper.toExistsItem(item, itemFromStorage), id));
    }

    @Override
    public ItemDto getItemById(int id) {
        Item itemFromStorage = itemDao.getItemById(id);
        if (itemFromStorage == null) {
            return null;
        }

        return ItemMapper.toItemDto(itemFromStorage);
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(int ownerId) {
        return itemDao.getAllItemsByOwnerId(ownerId).stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItemsByName(String name) {
        return itemDao.searchAvailableItemsByName(name).stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
