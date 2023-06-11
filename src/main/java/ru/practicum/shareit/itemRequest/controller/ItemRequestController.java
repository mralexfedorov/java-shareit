package ru.practicum.shareit.itemRequest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping()
    public ItemRequestDto addItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Создание запроса вещи {} пользователем с id = {}", itemRequestDto, userId);
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto getById(@PathVariable int itemRequestId,
                                  @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение бронирования {} пользователем c id = {}", itemRequestId, userId);
        return itemRequestService.getItemRequestById(itemRequestId, userId);
    }

    @GetMapping()
    public List<ItemRequestDto> getAllRequestByRequesterId(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получение всех запросов пользователя с id = {}", userId);
        return itemRequestService.getAllItemRequestsByRequesterId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") int userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "20") int size) {
        log.info("Получение всех запросов");
        return itemRequestService.getAllItemRequests(userId, PageRequest.of(from,
                size, Sort.by("created").descending()));

    }
}
