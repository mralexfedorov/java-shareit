package ru.practicum.shareit.item.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Data
@AllArgsConstructor
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getOwnerId() != 0 ? item.getOwnerId() : 0,
                item.getRequestId() != 0 ? item.getRequestId() : 0
        );
    }

    public static Item toItem(int ownerId, ItemDto item) {
        return new Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                ownerId,
                item.getRequestId() != 0 ? item.getRequestId() : 0
        );
    }

    public static Item toExistsItem(ItemDto itemDto, Item item) {
        String name = itemDto.getName();
        if (name != null) {
            item.setName(name);
        }
        String description = itemDto.getDescription();
        if (description != null) {
            item.setDescription(description);
        }
        Boolean available = itemDto.getAvailable();
        if (available != null) {
            item.setAvailable(available);
        }
        return item;
    }
}
