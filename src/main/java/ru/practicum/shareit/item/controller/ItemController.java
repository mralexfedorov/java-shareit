package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
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
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto addItem(@RequestHeader(USER_ID) int userId,
                           @RequestBody ItemDto itemDto) {
        log.info("Добавление предмета: " + itemDto);
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(USER_ID) int userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable("itemId") int id) {
        log.info("Редактирование предмета с id=" + id);
        return itemService.updateItem(userId, itemDto, id);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(USER_ID) int userId,
                               @PathVariable("itemId") int id) {
        log.info("Получение предмета с id=" + id);
        return itemService.getItemById(userId, id);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwnerId(@RequestHeader(USER_ID) int userId,
                                              @RequestParam(name = "from", defaultValue = "0") int from,
                                              @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Получение всех предметов пользователя с id=" + userId);
        return itemService.getAllItemsByOwnerId(userId, PageRequest.of(from, size));
    }


    @GetMapping("/search")
    public List<ItemDto> searchAvailableItemsByName(@RequestHeader(USER_ID) int userId,
                                                    @RequestParam(name = "text") String text,
                                                    @RequestParam(name = "from", defaultValue = "0") int from,
                                                    @RequestParam(name = "size", defaultValue = "20") int size) {
        log.info("Получение всех доступных предметов, содержащих в названии: " + text);
        return itemService.searchAvailableItemsByName(text, PageRequest.of(from, size));

    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto commentDto,
                              @RequestHeader(USER_ID) int userId,
                              @PathVariable int itemId) {
        log.info("Добавление комментария к " + itemId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}
