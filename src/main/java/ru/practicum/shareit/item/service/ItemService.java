package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(int ownerId, ItemDto itemDto);

    ItemDto updateItem(int ownerId, ItemDto itemDto, int id);

    ItemDto getItemById(int userId, int id);

    List<ItemDto> getAllItemsByOwnerId(int ownerId, Pageable pageable);

    List<ItemDto> searchAvailableItemsByName(String name, Pageable pageable);

    CommentDto addComment(int ownerId, int itemId, CommentDto commentDto);
}
