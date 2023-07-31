package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.getUserById(userId));
        log.debug("Добавлена вещь " + item.getName() + " пользователем с ID " + userId);
        return ItemMapper.ItemToDto(itemRepository.addItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId) {
        Item itemInStorage = itemRepository.getItemById(itemId);

        itemDto.setId(itemId);
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(itemInStorage.getAvailable());
        }
        if (itemDto.getName() == null) {
            itemDto.setName(itemInStorage.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(itemInStorage.getDescription());
        }

        Item updatedItem = itemRepository.updateItem(itemDto, userId);
        log.debug("Обновлены данные о вещи " + itemDto.getName() + " пользователем с ID " + userId);
        return ItemMapper.ItemToDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        return ItemMapper.ItemToDto(itemRepository.getItemById(itemId));
    }

    @Override
    public Collection<ItemDto> getUserItems(Integer userId) {
        User user = userRepository.getUserById(userId); // проверка существования пользователя
        log.debug("Получение информации о всех вещах пользователя " + user.getName() + " с ID " + userId);
        return ItemMapper.ItemsListToDto(itemRepository.getUserItems(user));
    }

    @Override
    public Collection<ItemDto> findItems(String searchKey) {
        log.debug("Поиск вещей по ключевому слову " + searchKey);
        return ItemMapper.ItemsListToDto(itemRepository.findItems(searchKey));
    }
}
