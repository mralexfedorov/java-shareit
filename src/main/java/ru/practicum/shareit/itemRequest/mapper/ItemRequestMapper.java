package ru.practicum.shareit.itemRequest.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toUserDto(itemRequest.getRequester()),
                itemRequest.getCreated(),
                null
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                UserMapper.toUser(itemRequestDto.getRequester()),
                itemRequestDto.getCreated()
        );
    }

    public static ItemRequestDto toItemRequestWitItemsDto(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toUserDto(itemRequest.getRequester()),
                itemRequest.getCreated(),
                items
        );
    }
}
