package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String OWNER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@RequestHeader(OWNER_ID) int ownerId,
                           @RequestBody ItemDto itemDto) {
        log.info("Добавление предмета: " + itemDto);
        return itemService.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(OWNER_ID) int ownerId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable("itemId") int id) {
        log.info("Редактирование предмета с id=" + id);
        return itemService.updateItem(ownerId, itemDto, id);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") int id) {
        log.info("Получение предмета с id=" + id);
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwnerId(@RequestHeader(OWNER_ID) int ownerId) {
        log.info("Получение всех предметов пользователя с id=" + ownerId);
        return itemService.getAllItemsByOwnerId(ownerId);
    }


    @GetMapping("/search")
    public List<ItemDto> searchAvailableItemsByName(@RequestHeader(OWNER_ID) int ownerId,
                                                      @RequestParam String text) {
        log.info("Получение всех доступных предметов, содержащих в названии: " + text);
        return itemService.searchAvailableItemsByName(text);

    }

}
