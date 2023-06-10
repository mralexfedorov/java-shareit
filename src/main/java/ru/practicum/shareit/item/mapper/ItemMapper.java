package ru.practicum.shareit.item.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Data
@UtilityClass
@AllArgsConstructor
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                UserMapper.toUserDto(item.getOwner()),
                item.getRequestId() != 0 ? item.getRequestId() : 0,
                null,
                null,
                null
        );
    }

    public static ItemDto toItemWithBookingsDto(Item item,
                                                Booking lastBooking,
                                                Booking nextBooking,
                                                List<CommentDto> comments) {
        BookingItemDto lastBookingItemDto = null;
        BookingItemDto nextBookingItemDto = null;

        if (lastBooking != null) {
            lastBookingItemDto = new BookingItemDto(
                    lastBooking.getId(),
                    lastBooking.getStart(),
                    lastBooking.getEnd(),
                    lastBooking.getBooker().getId()
            );
        }

        if (nextBooking != null) {
            nextBookingItemDto = new BookingItemDto(
                    nextBooking.getId(),
                    nextBooking.getStart(),
                    nextBooking.getEnd(),
                    nextBooking.getBooker().getId()
            );
        }

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                UserMapper.toUserDto(item.getOwner()),
                item.getRequestId() != 0 ? item.getRequestId() : 0,
                lastBookingItemDto,
                nextBookingItemDto,
                comments
        );
    }

    public static Item toItem(ItemDto item) {
        return new Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                UserMapper.toUser(item.getOwner()),
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
