package ru.practicum.shareit.itemRequest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.mapper.ItemRequestMapper;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, int requesterId) {
        User requester = userRepository.findById(requesterId).orElseThrow(() -> new NoSuchElementException(
                String.format("Пользователь с таким id %s не существует", requesterId)));
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException(
                    String.format("У запроса с id %s не заполнено описание", itemRequestDto.getId()));
        }
        itemRequestDto.setRequester(requester);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequestDto createdItemRequest = ItemRequestMapper.toItemRequestDto(itemRequestRepository
                .save(ItemRequestMapper.toItemRequest(itemRequestDto)));
        log.debug("Создан запрос {}.", requesterId);
        return createdItemRequest;
    }

    @Override
    public ItemRequestDto getItemRequestById(int requestId, int requesterId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NoSuchElementException(
                String.format("Запрос с таким id %s не существует", requestId)));

        User requester = userRepository.findById(requesterId).orElseThrow(() -> new NoSuchElementException(
                String.format("Пользователь с таким id %s не существует", requesterId)));

        return ItemRequestMapper.toItemRequestWitItemsDto(itemRequest, itemRepository.findAllByRequestId(requestId)
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestsByRequesterId(int requesterId) {
        User requester = userRepository.findById(requesterId).orElseThrow(() -> new NoSuchElementException(
                String.format("Пользователь с таким id %s не существует", requesterId)));

        List<ItemRequestDto> itemRequests = new ArrayList<>();

        for (ItemRequest itemRequest: itemRequestRepository.findAllByRequesterId(requesterId)) {
            itemRequests.add(ItemRequestMapper.toItemRequestWitItemsDto(itemRequest,
                    itemRepository.findAllByRequestId(itemRequest.getRequester().getId())
                            .stream().map(ItemMapper::toItemDto).collect(Collectors.toList())
            ));
        }

        return itemRequests;
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(int requesterId, Pageable pageable) {
        User requester = userRepository.findById(requesterId).orElseThrow(() -> new NoSuchElementException(
                String.format("Пользователь с таким id %s не существует", requesterId)));

        List<ItemRequestDto> itemRequests = new ArrayList<>();

        for (ItemRequest itemRequest: itemRequestRepository.findAllByRequesterIdNot(requesterId, pageable)) {
            itemRequests.add(ItemRequestMapper.toItemRequestWitItemsDto(itemRequest,
                    itemRepository.findAllByRequestId(itemRequest.getId())
                            .stream().map(ItemMapper::toItemDto).collect(Collectors.toList())
            ));
        }

        return itemRequests;
    }
}
