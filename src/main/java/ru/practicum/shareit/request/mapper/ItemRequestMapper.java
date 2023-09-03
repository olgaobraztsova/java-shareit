package ru.practicum.shareit.request.mapper;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemResponseDtoForRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequest toItemRequest(@NonNull ItemRequestShortDto itemRequestShortDto, User user, LocalDateTime created) {
        return ItemRequest.builder()
                .description(itemRequestShortDto.getDescription())
                .requester(user)
                .created(created)
                .build();
    }

    public ItemRequestDto toItemRequestDto(@NonNull ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester())
                .created(itemRequest.getCreated())
                .build();
    }

    public ItemRequestDtoWithResponses toItemRequestDtoWithResponses(
            ItemRequest itemRequest,
            List<ItemResponseDtoForRequest> itemResponseDtoForRequestList) {

        if (itemResponseDtoForRequestList == null) {
            itemResponseDtoForRequestList = Collections.emptyList();
        }

        return ItemRequestDtoWithResponses.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(UserMapper.userToDto(itemRequest.getRequester()))
                .created(itemRequest.getCreated())
                .items(itemResponseDtoForRequestList)
                .build();
    }
}
