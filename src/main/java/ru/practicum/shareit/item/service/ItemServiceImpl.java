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
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.getUserById(userId));
        log.info("Добавлена вещь {} пользователем с ID {}", item.getName(), userId);
        return ItemMapper.itemToDto(itemRepository.createItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId) {
        log.info("Обновлены данные о вещи {} пользователем с ID  {}", itemDto.getName(), userId);
        return ItemMapper.itemToDto(itemRepository.updateItem(itemDto, itemId, userId));
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        return ItemMapper.itemToDto(itemRepository.getItemById(itemId));
    }

    @Override
    public Collection<ItemDto> getUserItems(Integer userId) {
        User user = userRepository.getUserById(userId); // проверка существования пользователя
        log.info("Получение информации о всех вещах пользователя {} c ID {}", user.getName(), userId);
        return ItemMapper.itemsListToDto(itemRepository.getUserItems(user));
    }

    @Override
    public Collection<ItemDto> findItems(String searchKey) {
        log.info("Поиск вещей по ключевому слову {}", searchKey);
        if (searchKey.isBlank()) {
            return Collections.emptyList();
        }
        return ItemMapper.itemsListToDto(itemRepository.findItems(searchKey));
    }
}
