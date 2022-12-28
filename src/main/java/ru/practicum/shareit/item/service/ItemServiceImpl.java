package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public ItemDto addItem(int ownerId, ItemDto itemDto) {
        return itemStorage.addItem(ownerId, itemDto);
    }

    @Override
    public ItemDto updateItem(int ownerId, ItemDto itemDto, int id) {
        return itemStorage.updateItem(ownerId, itemDto, id);
    }

    @Override
    public ItemDto getItemById(int id) {
        return itemStorage.getItemById(id);
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(int ownerId) {
        return itemStorage.getAllItemsByOwnerId(ownerId);
    }

    @Override
    public List<ItemDto> getAvailableItemsByName(String name) {
        return itemStorage.getAvailableItemsByName(name);
    }
}
