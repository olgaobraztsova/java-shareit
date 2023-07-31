package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.ItemInvalidParameterException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class ItemRepository {

    private final Map<Integer, Item> itemMap = new HashMap<>();
    private Integer idCounter = 1;

    public Item addItem(Item item) {
        validateItem(item);
        item.setId(idCounter++);
        itemMap.put(item.getId(), item);
        return item;
    }

    public Item updateItem(ItemDto itemDto, Integer userId) {
        if (itemMap.get(itemDto.getId()) == null) {
            throw new ItemNotFoundException("Вещь с ID " + itemDto.getId() + " не найдена");
        }
        Item existingItem = itemMap.get(itemDto.getId());
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Пользователь может редактировать только свою вещь");
        }
        if (itemDto.getAvailable() != null && itemDto.getAvailable() != existingItem.getAvailable()) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            existingItem.setDescription(itemDto.getDescription());
        }
        itemMap.put(existingItem.getId(), existingItem);
        return existingItem;
    }

    public Item getItemById(Integer id) {
        if (!itemMap.containsKey(id)) {
            throw new ItemNotFoundException("Вещь с ID " + id + " не найдена");
        }
        return itemMap.get(id);
    }

    public Collection<Item> getUserItems(User user) {
        Collection<Item> userItems = new ArrayList<>();
        for (Item item : itemMap.values()) {
            if (item.getOwner().getId().equals(user.getId())) {
                userItems.add(item);
            }
        }
        return userItems;
    }

    public Collection<Item> findItems(String searchKey) {
        Collection<Item> searchResult = new ArrayList<>();

        if (searchKey.isBlank()) {
            return searchResult;
        }

        for (Item item : itemMap.values()) {
            if (item.getName().toLowerCase().contains(searchKey.toLowerCase())
                    || item.getDescription().toLowerCase().contains(searchKey.toLowerCase())) {
                if (item.getAvailable()) {
                    searchResult.add(item);
                }
            }
        }
        return searchResult;
    }

    private void validateItem(Item item) {
        if (item.getName().isBlank()) {
            throw new ItemInvalidParameterException("Поле name пустое");
        }
    }
}
