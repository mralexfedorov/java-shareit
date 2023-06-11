package ru.practicum.shareit.itemRequest.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(ItemRequestDto requestDto, int requesterId);

    ItemRequestDto getItemRequestById(int requestId, int requesterId);

    List<ItemRequestDto> getAllItemRequestsByRequesterId(int requesterId);

    List<ItemRequestDto> getAllItemRequests(int requesterId, Pageable pageable);
}
