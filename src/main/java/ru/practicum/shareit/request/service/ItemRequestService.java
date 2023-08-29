package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createItemRequest(Integer userId, ItemRequestShortDto itemRequestShortDto);

    List<ItemRequestDtoWithResponses> getItemRequestsByUserId(Integer userId);

    List<ItemRequestDtoWithResponses> getItemRequestsByOtherUsers(Integer userId, Integer from, Integer size);

    ItemRequestDtoWithResponses getItemRequestById(Integer itemId, Integer userId);

}
