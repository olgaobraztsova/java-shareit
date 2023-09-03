package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Integer userId);

    ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId);

    ItemResponseDto getItemById(Integer itemId, Integer userId);

    Collection<ItemResponseDto> getUserItems(Integer userId, Integer from, Integer size);

    Collection<ItemDto> findItems(String searchKey);

    CommentResponseDto postComment(CommentDto commentDto, Integer userId, Integer itemId);
}
