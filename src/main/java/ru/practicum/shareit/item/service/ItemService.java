package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, Integer userId);

    ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId);

    ItemDto getItemById(Integer ItemId);

    Collection<ItemDto> getUserItems(Integer userId);

    Collection<ItemDto> findItems(String searchKey);
}
