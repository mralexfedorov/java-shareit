package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.itemRequest.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto addItem(int ownerId, ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException(
                    String.format("Предмет с id %s не доступен", itemDto.getId()));
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException(
                    String.format("У предмета с id %s не заполнено описание", itemDto.getId()));
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException(
                    String.format("У предмета с id %s не заполнено название", itemDto.getId()));
        }
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NoSuchElementException(
                String.format("Пользователь с таким id %s не существует", ownerId)));
        itemDto.setOwner(UserMapper.toUserDto(owner));

        ItemDto createdItem = ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(itemDto)));
        log.debug("Создан предмет {}.", createdItem.getName());
        return createdItem;
    }

    @Transactional
    @Override
    public ItemDto updateItem(int ownerId, ItemDto itemDto, int id) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NoSuchElementException(
                String.format("Пользователь с таким id %s не существует", ownerId)));
        Item itemFromStorage = itemRepository.findByIdAndOwnerId(id, ownerId).orElseThrow(()
                -> new NoSuchElementException(String.format("Предмет с таким id %s не существует", id)));

        itemDto.setId(id);
        itemDto.setOwner(UserMapper.toUserDto(owner));
        ItemDto updatedItem = ItemMapper.toItemDto(itemRepository.save(
                ItemMapper.toExistsItem(itemDto, itemFromStorage)));

        log.debug("Данные о предмете {} обновлены.", updatedItem.getName());
        return updatedItem;
    }

    @Override
    public ItemDto getItemById(int userId, int id) {
        Item itemFromStorage = itemRepository.findById(id).orElseThrow(() -> new NoSuchElementException(
                String.format("Предмет с таким id %s не существует", id)));
        LocalDateTime now = LocalDateTime.now();
        return ItemMapper.toItemWithBookingsDto(itemFromStorage,
                bookingRepository.findAllByItemIdAndItemOwnerIdAndStartBeforeOrderByStartDesc(id, userId, now)
                        .stream()
                        .max(Comparator.comparing(Booking::getEnd))
                        .orElse(null),
                bookingRepository.findAllByItemIdAndItemOwnerIdAndStatusAndStartAfterOrderByStartDesc(id, userId,
                                BookingStatus.APPROVED, now)
                        .stream()
                        .min(Comparator.comparing(Booking::getStart))
                        .orElse(null),
                commentRepository.getAllByItemId(id)
                        .stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()));
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(int ownerId, Pageable pageable) {
        List<ItemDto> itemsDto = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Item item: itemRepository.findAllByOwnerId(ownerId, pageable)) {
            itemsDto.add(ItemMapper.toItemWithBookingsDto(item,
                    bookingRepository.findAllByItemIdAndItemOwnerIdAndStartBeforeOrderByStartDesc(item.getId(),
                                    ownerId, now)
                            .stream()
                            .max(Comparator.comparing(Booking::getEnd))
                            .orElse(null),
                    bookingRepository.findAllByItemIdAndItemOwnerIdAndStatusAndStartAfterOrderByStartDesc(item.getId(),
                                    ownerId, BookingStatus.APPROVED, now)
                            .stream()
                            .min(Comparator.comparing(Booking::getStart))
                            .orElse(null),
                    commentRepository.getAllByItemId(item.getId())
                            .stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList())));
        }

        return itemsDto;
    }

    @Override
    public List<ItemDto> searchAvailableItemsByName(String name, Pageable pageable) {
        if (name.isEmpty()) {
            return new ArrayList<>();
        }

        return itemRepository.search(name, pageable).stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addComment(int ownerId, int itemId, CommentDto commentDto) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new NoSuchElementException(
                String.format("Пользователь с таким id %s не существует", ownerId)));
        if (bookingRepository.findAllByBookerIdAndStatus(ownerId, BookingStatus.APPROVED,
                PageRequest.of(0, 20)).isEmpty()) {
            throw new ValidationException(String.format("Пользователь с id %s не бронировал предмет с id %s",
                    ownerId, itemId));
        }
        if (!bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(ownerId,
                        LocalDateTime.now().plusDays(1), PageRequest.of(0, 20)).isEmpty()) {
            throw new ValidationException(String.format("Пользователь с id %s забронировал предмет с id %s в будущем",
                    ownerId, itemId));
        }
        if (commentDto.getText() == null || commentDto.getText().equals("")) {
            throw new ValidationException("Отзыв пуст");
        }
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException(
                String.format("Предмет с таким id %s не существует", itemId)));

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
