package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemResponseDtoForRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestDto createItemRequest(Integer userId, ItemRequestShortDto itemRequestShortDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestShortDto, user, LocalDateTime.now());
        log.info("Пользователем с ID {} добавлен запрос на вещь с описанием: {} ", userId, itemRequest.getDescription());
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Transactional
    @Override
    public List<ItemRequestDtoWithResponses> getItemRequestsByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);

        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, List<ItemResponseDtoForRequest>> itemsForRequests = getItemsForRequests(itemRequests);

        List<ItemRequestDtoWithResponses> itemRequestsWithResponses = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) {
            itemRequestsWithResponses.add(
                    ItemRequestMapper.toItemRequestDtoWithResponses(
                            itemRequest,
                            itemsForRequests.get(itemRequest.getId())));
        }

        return itemRequestsWithResponses;
    }

    @Transactional
    @Override
    public List<ItemRequestDtoWithResponses> getItemRequestsByOtherUsers(Integer userId, Integer from, Integer size) {

        Pageable page = PageRequest.of((int) from / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNot(userId, page);

        if (itemRequests.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, List<ItemResponseDtoForRequest>> itemsForRequests = getItemsForRequests(itemRequests);

        List<ItemRequestDtoWithResponses> itemRequestsWithResponses = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) {
            itemRequestsWithResponses.add(
                    ItemRequestMapper.toItemRequestDtoWithResponses(
                            itemRequest,
                            itemsForRequests.get(itemRequest.getId())));
        }

        return itemRequestsWithResponses;
    }

    @Transactional
    @Override
    public ItemRequestDtoWithResponses getItemRequestById(Integer requestId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос с ID " + requestId + " не найден"));

        Map<Integer, List<ItemResponseDtoForRequest>> itemsForRequests = getItemsForRequests(List.of(itemRequest));

        return ItemRequestMapper.toItemRequestDtoWithResponses(
                itemRequest,
                itemsForRequests.get(itemRequest.getId()));
    }


    private Map<Integer, List<ItemResponseDtoForRequest>> getItemsForRequests(List<ItemRequest> itemRequests) {

        // список вещей, созданных в ответ на запросы
        List<Item> items = itemRepository.findAllByRequestIdIn(itemRequests
                        .stream()
                        .map(ItemRequest::getId)
                .collect(Collectors.toList()));

        Map<Integer, List<ItemResponseDtoForRequest>> itemsByRequestId = new HashMap<>();

        for (Item item : items) {
            Integer requestId = item.getRequest().getId();
            if (itemsByRequestId.containsKey(requestId)) {
                List<ItemResponseDtoForRequest> itemsInMap = itemsByRequestId.get(requestId);
                itemsInMap.add(ItemMapper.toItemResponseDtoForRequest(item, requestId));
            } else {
                List<ItemResponseDtoForRequest> itemsList = new ArrayList<>();
                itemsList.add(ItemMapper.toItemResponseDtoForRequest(item, requestId));
                itemsByRequestId.put(requestId, itemsList);
            }
        }

        return itemsByRequestId;
    }
}
