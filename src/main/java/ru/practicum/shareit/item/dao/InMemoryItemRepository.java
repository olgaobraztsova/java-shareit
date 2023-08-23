package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;

public class InMemoryItemRepository {

    private final Map<Integer, Item> itemMap = new HashMap<>();
    private Integer idCounter = 1;

    public Item createItem(Item item) {
        item.setId(idCounter++);
        itemMap.put(item.getId(), item);
        return item;
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
