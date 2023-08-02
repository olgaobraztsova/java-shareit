package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class ItemRepository {

    private final Map<Integer, Item> itemMap = new HashMap<>();
    private Integer idCounter = 1;

    public Item createItem(Item item) {
        item.setId(idCounter++);
        itemMap.put(item.getId(), item);
        return item;
    }

    public Item updateItem(ItemDto itemDto, Integer itemId, Integer userId) {
        if (itemMap.get(itemId) == null) {
            throw new EntityNotFoundException("Вещь с ID " + itemDto.getId() + " не найдена");
        }

        Item existingItem = itemMap.get(itemId);
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Пользователь может редактировать только свою вещь");
        }

        String name = itemDto.getName();
        if (name != null && !name.isBlank()) {
            existingItem.setName(name);
        }

        String description = itemDto.getDescription();
        if (description != null && !description.isBlank()) {
            existingItem.setDescription(description);
        }

        Boolean available = itemDto.getAvailable();
        if (available != null && existingItem.getAvailable() != available) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return existingItem;
    }

    public Item getItemById(Integer id) {
        if (!itemMap.containsKey(id)) {
            throw new EntityNotFoundException("Вещь с ID " + id + " не найдена");
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
        String lowerCaseKey = searchKey.toLowerCase();

        for (Item item : itemMap.values()) {
            if (item.getName().toLowerCase().contains(lowerCaseKey)
                    || item.getDescription().toLowerCase().contains(lowerCaseKey)) {
                if (item.getAvailable()) {
                    searchResult.add(item);
                }
            }
        }
        return searchResult;
    }
}
