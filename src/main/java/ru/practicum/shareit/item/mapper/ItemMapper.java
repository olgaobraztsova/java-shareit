package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDtoForRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {

    public Item toItem(ItemDto itemDto) {

        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemDto itemToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId((item.getRequest() != null) ? item.getRequest().getId() : null)
                .ownerId(item.getOwner().getId())
                .build();
    }

    public ItemResponseDto itemToItemResponseDto(Item item,
                                                 Booking lastBooking,
                                                 Booking nextBooking, List<Comment> comments) {

        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(item.getId());
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setAvailable(item.getAvailable());
        itemResponseDto.setOwner(UserMapper.userToDto(item.getOwner()));
        itemResponseDto.setComments(CommentMapper.commentsListToDto(comments));

        if (lastBooking != null) {
            itemResponseDto.setLastBooking(BookingMapper.bookingToBookingShortDto(lastBooking));
        }
        if (nextBooking != null) {
            itemResponseDto.setNextBooking(BookingMapper.bookingToBookingShortDto(nextBooking));
        }

        return itemResponseDto;
    }

    public Collection<ItemDto> itemsListToDto(Collection<Item> items) {
        return items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList());
    }

    public ItemResponseDtoForRequest toItemResponseDtoForRequest(Item item, Integer requestId) {
        return ItemResponseDtoForRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .requestId(requestId)
                .available(item.getAvailable())
                .build();
    }

}
