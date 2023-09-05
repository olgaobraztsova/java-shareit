package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemWithBookingsAndComments;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, Integer userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден")));
        if (itemDto.getRequestId() != null) {
            Integer requestId = itemDto.getRequestId();
            ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new EntityNotFoundException("Запрос с ID " + requestId + " не найден"));
            item.setRequest(itemRequest);
        }

        log.info("Добавлена вещь {} пользователем с ID {}", item.getName(), userId);
        return ItemMapper.itemToDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь с ID " + itemId + " не найдена"));
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

        itemRepository.save(existingItem);
        log.info("Обновлены данные о вещи {} пользователем с ID  {}", itemDto.getName(), userId);
        return ItemMapper.itemToDto(existingItem);
    }

    @Transactional
    @Override
    public ItemResponseDto getItemById(Integer itemId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь с ID " + itemId + " не найдена"));

        ItemResponseDto itemResponseDto;
        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(itemId, Status.APPROVED, now);
            nextBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(itemId, Status.APPROVED, now);
        }

        List<Comment> comments = commentRepository.findByItemId(itemId);
        if (comments.isEmpty()) {
            itemResponseDto = ItemMapper.itemToItemResponseDto(item, lastBooking, nextBooking, Collections.emptyList());

        } else {
            itemResponseDto = ItemMapper.itemToItemResponseDto(item, lastBooking, nextBooking, comments);
        }

        log.info("Запрос информации о вещи {} пользователем c ID {}", itemId, userId);
        return itemResponseDto;
    }

    @Transactional
    @Override
    public Collection<ItemResponseDto> getUserItems(Integer ownerId, Integer from, Integer size) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + ownerId + " не найден"));

        Pageable page = PageRequest.of((int) from / size, size);

        LocalDateTime now = LocalDateTime.now();

        List<Item> userItems = itemRepository.findAllByOwnerIdOrderById(ownerId, page);

        List<Booking> approvedBookings = bookingRepository
                .findApprovedBookingsFor(userItems, Sort.by(Sort.Direction.DESC, "start"));

        Map<Integer, List<Booking>> approvedBookingsByItem = approvedBookings
                .stream()
                .collect(Collectors.groupingBy(x -> x.getItem().getId()));

        List<Comment> itemComments =
                commentRepository.findCommentsForItemsIn(userItems, Sort.by(Sort.Direction.ASC, "id"));
        Map<Integer, List<Comment>> comments = itemComments
                .stream()
                .collect(Collectors.groupingBy(x -> x.getItem().getId()));

        List<ItemWithBookingsAndComments> userItemsWithBookings = userItems.stream()
                .map(item -> {
                    return new ItemWithBookingsAndComments(
                            item,
                            approvedBookingsByItem.getOrDefault(item.getId(), Collections.emptyList()),
                            comments.getOrDefault(item.getId(), Collections.emptyList())
                    );
                }).collect(Collectors.toList());

        List<ItemResponseDto> itemResponseDtoList = new ArrayList<>();

        for (ItemWithBookingsAndComments itemWithBookingsAndComments : userItemsWithBookings) {
            Item item = itemWithBookingsAndComments.getItem();
            List<Booking> bookings = itemWithBookingsAndComments.getBookings();
            Booking lastBooking = null;
            Booking nextBooking = null;
            if (!bookings.isEmpty()) {
                lastBooking = findLastBooking(bookings, now);
                nextBooking = findNextBooking(bookings, now);
            }

            itemResponseDtoList.add(ItemMapper.itemToItemResponseDto(
                    item,
                    lastBooking,
                    nextBooking,
                    itemWithBookingsAndComments.getComments())
            );
        }

        Collections.sort(itemResponseDtoList);

        log.info("Получение информации о всех вещах пользователя {} c ID {}", user.getName(), ownerId);
        return itemResponseDtoList;
    }

    @Transactional
    @Override
    public Collection<ItemDto> findItems(String searchKey) {
        if (searchKey.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Поиск вещей по ключевому слову {}", searchKey);
        return ItemMapper.itemsListToDto(itemRepository.findBySearchKey(searchKey));
    }

    @Transactional
    @Override
    public CommentResponseDto postComment(CommentDto commentDto, Integer userId, Integer itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Вещь с ID " + itemId + " не найдена"));

        LocalDateTime now = LocalDateTime.now();

        //  check user has rented the item
        Booking booking = bookingRepository
                .findFirstByItemIdAndBookerIdAndStatusAndEndBefore(
                        itemId, userId, Status.APPROVED, now);

        if (booking == null) {
            throw new AvailabilityException("Пользователь с ID " + userId + " не бронировал вещь с ID " + itemId);
        }

        Comment comment = CommentMapper.toComment(commentDto, item, user, now);

        log.info("Публикация отзыва пользователем с ID {} к вещи с ID {}", userId, itemId);
        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }


    private Booking findLastBooking(List<Booking> bookings, LocalDateTime now) {
        if (bookings != null && !bookings.isEmpty()) {
            Booking lastBooking = null;
            for (Booking booking : bookings) {
                if (booking.getEnd().isBefore(now)) {
                    if (lastBooking == null) {
                        lastBooking = booking;
                    } else if (lastBooking.getEnd().isAfter(booking.getEnd())) {
                        lastBooking = booking;
                    }
                }
            }
            return lastBooking;
        } else {
            throw new EntityNotFoundException("У вещи нет бронирований");
        }
    }

    private Booking findNextBooking(List<Booking> bookings, LocalDateTime now) {

        if (bookings != null && !bookings.isEmpty()) {
            Booking nextBooking = null;
            for (Booking booking : bookings) {
                if (booking.getStart().isAfter(now)) {
                    if (nextBooking == null) {
                        nextBooking = booking;
                    } else if (booking.getStart().isBefore(nextBooking.getStart())) {
                        nextBooking = booking;
                    }
                }
            }
            return nextBooking;
        } else {
            throw new EntityNotFoundException("У вещи нет бронирований");
        }
    }
}
