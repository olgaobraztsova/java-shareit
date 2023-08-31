package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @InjectMocks
    ItemServiceImpl itemService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;

    User owner;
    User requester;
    User booker;
    UserDto ownerDto;
    UserDto requesterDto;
    ItemRequest itemRequest;
    Item item;
    Item item2;
    ItemDto itemDto;
    Item updatedItem;
    ItemDto updatedItemDto;
    ItemResponseDto itemResponseDtoExpected;
    ItemResponseDto itemResponseDtoExpected2;
    Booking nextBooking;
    Booking lastBooking;
    CommentDto commentDto;
    CommentResponseDto commentResponseDto;
    Comment comment;


    @BeforeEach
    void beforeEach() {

        LocalDateTime now = LocalDateTime.now();
        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                bookingRepository,
                commentRepository,
                itemRequestRepository);

        owner = new User(1, "user name 1", "email1@email.com");
        requester = new User(2, "user name 2", "email2@email.com");
        booker = new User(3, "user name 3", "email3@email.com");

        ownerDto = new UserDto(1, "user name 1", "email1@email.com");
        requesterDto = new UserDto(2, "user name 2", "email2@email.com");

        itemRequest = new ItemRequest(
                1,
                "Описание запроса",
                requester,
                now.minusHours(2));

        item = Item.builder()
                .id(1)
                .name("вещь")
                .description("описание вещи")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build();

        item2 = Item.builder()
                .id(2)
                .name("вещь 2")
                .description("описание вещи 2")
                .available(true)
                .owner(owner)
                .build();

        updatedItem = Item.builder()
                .id(1)
                .name("новая вещь")
                .description("новое описание вещи")
                .request(itemRequest)
                .available(true)
                .owner(owner)
                .build();

        itemDto = ItemDto.builder()
                .id(1)
                .name("вещь")
                .description("описание вещи")
                .available(true)
                .ownerId(owner.getId())
                .requestId(itemRequest.getId())
                .build();

        updatedItemDto = ItemDto.builder()
                .name("новая вещь")
                .description("новое описание вещи")
                .available(true)
                .ownerId(owner.getId())
                .requestId(itemRequest.getId())
                .build();

        commentDto = new CommentDto("новый комментарий");

        commentResponseDto = new CommentResponseDto(
                1,
                "новый комментарий",
                itemDto,
                booker.getName(),
                now.minusDays(1));

        comment = new Comment(
                1,
                "новый комментарий",
                item,
                booker,
                now.minusDays(1));


        BookingShortDto nextBookingDto = new BookingShortDto(
                1,
                now.plusDays(2),
                now.plusDays(3),
                booker.getId(),
                Status.APPROVED
        );

        BookingShortDto lastBookingDto = new BookingShortDto(
                1,
                now.minusDays(3),
                now.minusDays(2),
                booker.getId(),
                Status.APPROVED
        );

        nextBooking = new Booking(
                1,
                now.plusDays(2),
                now.plusDays(3),
                item,
                booker,
                Status.APPROVED
        );

        lastBooking = new Booking(
                1,
                now.minusDays(3),
                now.minusDays(2),
                item,
                booker,
                Status.APPROVED
        );

        itemResponseDtoExpected = ItemResponseDto.builder()
                .id(1)
                .name("вещь")
                .description("описание вещи")
                .available(true)
                .owner(ownerDto)
                .nextBooking(nextBookingDto)
                .lastBooking(lastBookingDto)
                .comments(Collections.emptyList())
                .build();

        itemResponseDtoExpected2 = ItemResponseDto.builder()
                .id(2)
                .name("вещь 2")
                .description("описание вещи 2")
                .available(true)
                .owner(ownerDto)
                .comments(Collections.emptyList())
                .build();

    }

    @Test
    void testCreateItem() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        Mockito.when(itemRequestRepository.findById(any())).thenReturn(Optional.ofNullable(itemRequest));
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemActual = itemService.createItem(itemDto, owner.getId());

        assertThat(itemActual.getId(), is(item.getId()));
        assertThat(itemActual.getName(), is(item.getName()));
        assertThat(itemActual.getDescription(), is(item.getDescription()));
        assertThat(itemActual.getAvailable(), is(item.getAvailable()));
        assertThat(itemActual.getOwnerId(), is(item.getOwner().getId()));
        assertThat(itemActual.getRequestId(), is(itemRequest.getId()));

    }

    @Test
    void testUpdateItem() {
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        Mockito.when(itemRepository.save(any())).thenReturn(updatedItem);

        ItemDto itemActual = itemService.updateItem(updatedItemDto, updatedItemDto.getId(), owner.getId());

        assertThat(itemActual.getId(), is(updatedItem.getId()));
        assertThat(itemActual.getName(), is(updatedItem.getName()));
        assertThat(itemActual.getDescription(), is(updatedItem.getDescription()));
        assertThat(itemActual.getAvailable(), is(updatedItem.getAvailable()));
        assertThat(itemActual.getOwnerId(), is(updatedItem.getOwner().getId()));
        assertThat(itemActual.getRequestId(), is(updatedItem.getRequest().getId()));
    }

    @Test
    void testUpdateItemWhenUserNotOwner() {
        Mockito.when(itemRepository.findById(any()))
                .thenThrow(new AccessDeniedException("Пользователь может редактировать только свою вещь"));

        final AccessDeniedException exception = Assertions.assertThrows(
                AccessDeniedException.class,
                () -> itemService.updateItem(updatedItemDto, 1, 3));

        Assertions.assertEquals("403 FORBIDDEN \"Пользователь может редактировать только свою вещь\"",
                exception.getMessage());
    }

    @Test
    void testGetItemById() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        Mockito.when(commentRepository.findByItemId(any())).thenReturn(Collections.emptyList());
        Mockito.when(bookingRepository
                .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(any(), any(), any())).thenReturn(nextBooking);
        Mockito.when(bookingRepository
                .findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(any(), any(), any())).thenReturn(lastBooking);

        ItemResponseDto itemResponseDtoActual = itemService.getItemById(item.getId(), owner.getId());

        assertThat(itemResponseDtoActual.getId(), is(itemResponseDtoExpected.getId()));
        assertThat(itemResponseDtoActual.getDescription(), is(itemResponseDtoExpected.getDescription()));
        assertThat(itemResponseDtoActual.getAvailable(), is(itemResponseDtoExpected.getAvailable()));
        assertThat(itemResponseDtoActual.getOwner(), is(itemResponseDtoExpected.getOwner()));
        assertThat(itemResponseDtoActual.getLastBooking(), is(itemResponseDtoExpected.getLastBooking()));
        assertThat(itemResponseDtoActual.getNextBooking(), is(itemResponseDtoExpected.getNextBooking()));
        assertThat(itemResponseDtoActual.getComments(), is(Collections.emptyList()));
    }

    @Test
    void testGetUserItems() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        Mockito.when(itemRepository.findAllByOwnerIdOrderById(any(), any())).thenReturn(List.of(item, item2));
        Mockito.when(bookingRepository.findApprovedBookingsFor(any(), any())).thenReturn(List.of(lastBooking, nextBooking));
        Mockito.when(commentRepository.findCommentsForItemsIn(any(), any())).thenReturn(Collections.emptyList());

        Collection<ItemResponseDto> itemResponseDtoActual = itemService.getUserItems(owner.getId(), 0, 10);

        ArrayList<ItemResponseDto> itemResponseDtoArrayList = new ArrayList<>(itemResponseDtoActual);

        assertThat(itemResponseDtoActual.size(), is(List.of(itemResponseDtoExpected, itemResponseDtoExpected2).size()));
        assertThat(itemResponseDtoArrayList.get(0).getId(), is(itemResponseDtoExpected.getId()));
        assertThat(itemResponseDtoArrayList.get(0).getDescription(), is(itemResponseDtoExpected.getDescription()));
        assertThat(itemResponseDtoArrayList.get(0).getOwner(), is(itemResponseDtoExpected.getOwner()));
        assertThat(itemResponseDtoArrayList.get(0).getAvailable(), is(itemResponseDtoExpected.getAvailable()));
        assertThat(itemResponseDtoArrayList.get(0).getLastBooking(), is(itemResponseDtoExpected.getLastBooking()));
        assertThat(itemResponseDtoArrayList.get(0).getNextBooking(), is(itemResponseDtoExpected.getNextBooking()));
        assertThat(itemResponseDtoArrayList.get(0).getComments(), is(itemResponseDtoExpected.getComments()));

        assertThat(itemResponseDtoArrayList.get(1).getId(), is(itemResponseDtoExpected2.getId()));
        assertThat(itemResponseDtoArrayList.get(1).getDescription(), is(itemResponseDtoExpected2.getDescription()));
        assertThat(itemResponseDtoArrayList.get(1).getOwner(), is(itemResponseDtoExpected2.getOwner()));
        assertThat(itemResponseDtoArrayList.get(1).getAvailable(), is(itemResponseDtoExpected2.getAvailable()));
        assertThat(itemResponseDtoArrayList.get(1).getLastBooking(), is(itemResponseDtoExpected2.getLastBooking()));
        assertThat(itemResponseDtoArrayList.get(1).getNextBooking(), is(itemResponseDtoExpected2.getNextBooking()));
        assertThat(itemResponseDtoArrayList.get(1).getComments(), is(itemResponseDtoExpected2.getComments()));

    }

    @Test
    void testPostComments() {

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository
                        .findFirstByItemIdAndBookerIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(lastBooking);
        Mockito.when(commentRepository.save(any())).thenReturn(comment);

        CommentResponseDto commentResponseDtoActual = itemService.postComment(commentDto, booker.getId(), item.getId());

        assertThat(commentResponseDto.getText(), is(commentResponseDtoActual.getText()));
        assertThat(commentResponseDto.getAuthorName(), is(commentResponseDtoActual.getAuthorName()));
        assertThat(commentResponseDto.getCreated(), is(commentResponseDtoActual.getCreated()));
        assertThat(commentResponseDto.getItemDto(), is(commentResponseDtoActual.getItemDto()));
    }

    @Test
    void testPostCommentsWhenUserHaveNotBookedItem() {

        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository
                        .findFirstByItemIdAndBookerIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(null);

        final AvailabilityException exception = Assertions.assertThrows(
                AvailabilityException.class,
                () -> itemService.postComment(commentDto, booker.getId(), item.getId()));

        Assertions.assertEquals("400 BAD_REQUEST \"Пользователь с ID "
                        + booker.getId() + " не бронировал вещь с ID " + item.getId() + "\"",
                exception.getMessage());
    }

    @Test
    void testFindItems() {
        Mockito.when(itemRepository.findBySearchKey(any())).thenReturn(List.of(item));

        Collection<ItemDto> itemResponseDtoActual = itemService.findItems("вещь");

        ArrayList<ItemDto> itemResponseDtoArrayList = new ArrayList<>(itemResponseDtoActual);

        assertThat(itemResponseDtoActual.size(), is(List.of(itemDto).size()));
        assertThat(itemResponseDtoArrayList.get(0).getId(), is(itemDto.getId()));
        assertThat(itemResponseDtoArrayList.get(0).getDescription(), is(itemDto.getDescription()));
        assertThat(itemResponseDtoArrayList.get(0).getOwnerId(), is(itemDto.getOwnerId()));
        assertThat(itemResponseDtoArrayList.get(0).getAvailable(), is(itemDto.getAvailable()));
        assertThat(itemResponseDtoArrayList.get(0).getName(), is(itemDto.getName()));
    }

    @Test
    void testFindItemsWhenSearchKeyIsEmpty() {
        Collection<ItemDto> itemResponseDtoActual = itemService.findItems("");

        assertThat(itemResponseDtoActual.size(), is(0));
    }
}
