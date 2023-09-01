package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    private UserDto bookerDto;
    private User booker;
    private User owner;
    private Item item;
    private ItemDto itemDto;
    private Booking booking;
    private BookingResponseDto bookingResponseDto;
    private BookingDto bookingDto;


    @BeforeEach
    void beforeEach() {

        LocalDateTime now = LocalDateTime.now();
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);

        owner = new User(1, "user name 1", "user1@email.com");
        booker = new User(2, "user name 2", "user2@email.com");
        bookerDto = new UserDto(2, "user name 2", "user2@email.com");

        item = Item.builder()
                .id(1)
                .name("вещь")
                .description("описание вещи")
                .owner(owner)
                .available(true)
                .build();

        itemDto = ItemDto.builder()
                .id(1)
                .name("вещь")
                .description("описание вещи")
                .ownerId(1)
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1)
                .item(itemDto)
                .booker(bookerDto)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .status(Status.WAITING)
                .build();

        booking = Booking.builder()
                .id(1)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
    }

    @Test
    void testCreateBooking() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponseDto bookingResponseDtoActual = bookingService.createBooking(bookerDto.getId(), bookingDto);

        assertThat(bookingResponseDtoActual.getId(), is(bookingResponseDto.getId()));
        assertThat(bookingResponseDtoActual.getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(bookingResponseDtoActual.getItem(), is(bookingResponseDto.getItem()));
        assertThat(bookingResponseDtoActual.getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(bookingResponseDtoActual.getStart(), is(bookingResponseDto.getStart()));
        assertThat(bookingResponseDtoActual.getEnd(), is(bookingResponseDto.getEnd()));
    }

    @Test
    void testApproveBooking() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));

        booking.setStatus(Status.WAITING);
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponseDto bookingResponseDtoActual =
                bookingService.approveBooking(owner.getId(), booking.getId(), true);

        assertThat(bookingResponseDtoActual.getStatus(), is(booking.getStatus()));
        assertThat(bookingResponseDtoActual.getId(), is(bookingResponseDto.getId()));
        assertThat(bookingResponseDtoActual.getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(bookingResponseDtoActual.getItem(), is(bookingResponseDto.getItem()));
        assertThat(bookingResponseDtoActual.getStart(), is(bookingResponseDto.getStart()));
        assertThat(bookingResponseDtoActual.getEnd(), is(bookingResponseDto.getEnd()));
    }

    @Test
    void testGetBookingById() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));

        BookingResponseDto bookingResponseDtoActual = bookingService
                .getBookingById(booker.getId(), booking.getId());

        assertThat(bookingResponseDtoActual.getId(), is(bookingResponseDto.getId()));
        assertThat(bookingResponseDtoActual.getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(bookingResponseDtoActual.getItem(), is(bookingResponseDto.getItem()));
        assertThat(bookingResponseDtoActual.getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(bookingResponseDtoActual.getStart(), is(bookingResponseDto.getStart()));
        assertThat(bookingResponseDtoActual.getEnd(), is(bookingResponseDto.getEnd()));

    }

    @Test
    void testGetBookingByIdWhenUserNotOwnerNotBooker() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findById(any())).thenReturn(Optional.ofNullable(booking));

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getBookingById(3, 1));

        Assertions.assertEquals("404 NOT_FOUND \"Доступ к бронированию возможен только владельцем " +
                        "вещи или пользователем, совершившим данное бронирование\"",
                exception.getMessage());
    }

    @Test
    void testGetAllBookingsByUserWhenStateIsAll() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findByBookerIdOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingResponseDto> listOfBookingsActual =
                bookingService.getAllBookingsByUser(booker.getId(), "ALL", 0, 10);

        assertThat(listOfBookingsActual.size(), is(1));
        assertThat(listOfBookingsActual.get(0).getId(), is(bookingResponseDto.getId()));
        assertThat(listOfBookingsActual.get(0).getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(listOfBookingsActual.get(0).getItem(), is(bookingResponseDto.getItem()));
        assertThat(listOfBookingsActual.get(0).getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));
        assertThat(listOfBookingsActual.get(0).getEnd(), is(bookingResponseDto.getEnd()));
    }

    @Test
    void testGetAllBookingsByUserWhenStateIsWaiting() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> listOfBookingsActual =
                bookingService.getAllBookingsByUser(booker.getId(), "WAITING", 0, 10);

        assertThat(listOfBookingsActual.size(), is(1));
        assertThat(listOfBookingsActual.get(0).getId(), is(bookingResponseDto.getId()));
        assertThat(listOfBookingsActual.get(0).getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(listOfBookingsActual.get(0).getItem(), is(bookingResponseDto.getItem()));
        assertThat(listOfBookingsActual.get(0).getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));
        assertThat(listOfBookingsActual.get(0).getEnd(), is(bookingResponseDto.getEnd()));
    }

    @Test
    void testGetAllBookingsByUserWhenStateIsCurrent() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> listOfBookingsActual =
                bookingService.getAllBookingsByUser(booker.getId(), "CURRENT", 0, 10);

        assertThat(listOfBookingsActual.size(), is(1));
        assertThat(listOfBookingsActual.get(0).getId(), is(bookingResponseDto.getId()));
        assertThat(listOfBookingsActual.get(0).getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(listOfBookingsActual.get(0).getItem(), is(bookingResponseDto.getItem()));
        assertThat(listOfBookingsActual.get(0).getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));
        assertThat(listOfBookingsActual.get(0).getEnd(), is(bookingResponseDto.getEnd()));
    }

    @Test
    void testGetAllBookingsByUserWhenStateIsPast() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> listOfBookingsActual =
                bookingService.getAllBookingsByUser(booker.getId(), "PAST", 0, 10);

        assertThat(listOfBookingsActual.size(), is(1));
        assertThat(listOfBookingsActual.get(0).getId(), is(bookingResponseDto.getId()));
        assertThat(listOfBookingsActual.get(0).getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(listOfBookingsActual.get(0).getItem(), is(bookingResponseDto.getItem()));
        assertThat(listOfBookingsActual.get(0).getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));
        assertThat(listOfBookingsActual.get(0).getEnd(), is(bookingResponseDto.getEnd()));
    }

    @Test
    void testGetAllBookingsByUserWhenStateIsFuture() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> listOfBookingsActual =
                bookingService.getAllBookingsByUser(booker.getId(), "FUTURE", 0, 10);

        assertThat(listOfBookingsActual.size(), is(1));
        assertThat(listOfBookingsActual.get(0).getId(), is(bookingResponseDto.getId()));
        assertThat(listOfBookingsActual.get(0).getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(listOfBookingsActual.get(0).getItem(), is(bookingResponseDto.getItem()));
        assertThat(listOfBookingsActual.get(0).getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));
        assertThat(listOfBookingsActual.get(0).getEnd(), is(bookingResponseDto.getEnd()));
    }

    @Test
    void testGetAllBookingsByUserWhenStateIsRejected() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(booker));
        Mockito.when(bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> listOfBookingsActual =
                bookingService.getAllBookingsByUser(booker.getId(), "REJECTED", 0, 10);

        assertThat(listOfBookingsActual.size(), is(1));
        assertThat(listOfBookingsActual.get(0).getId(), is(bookingResponseDto.getId()));
        assertThat(listOfBookingsActual.get(0).getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(listOfBookingsActual.get(0).getItem(), is(bookingResponseDto.getItem()));
        assertThat(listOfBookingsActual.get(0).getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));
        assertThat(listOfBookingsActual.get(0).getEnd(), is(bookingResponseDto.getEnd()));
    }

    @Test
    void testCreateBookingWhenItemNotAvailable() {
        item.setAvailable(false);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(booker));
        Mockito.when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));

        final AvailabilityException exception = Assertions.assertThrows(
                AvailabilityException.class,
                () -> bookingService.createBooking(booker.getId(), bookingDto));

        Assertions.assertEquals("400 BAD_REQUEST \"Вещь с ID 1 недоступна для бронирования\"",
                exception.getMessage());
    }

    @Test
    void testGetAllItemBookingsByOwnerWhenStateIsAll() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(owner));
        Mockito.when(bookingRepository.findByItemOwnerIdOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingResponseDto> listOfBookingsActual =
                bookingService.getAllItemBookingsByOwner(owner.getId(), "ALL", 0, 10);

        assertThat(listOfBookingsActual.size(), is(1));
        assertThat(listOfBookingsActual.get(0).getId(), is(bookingResponseDto.getId()));
        assertThat(listOfBookingsActual.get(0).getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(listOfBookingsActual.get(0).getItem(), is(bookingResponseDto.getItem()));
        assertThat(listOfBookingsActual.get(0).getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));
        assertThat(listOfBookingsActual.get(0).getEnd(), is(bookingResponseDto.getEnd()));
    }

    @Test
    void testGetAllItemBookingsByOwnerWhenStateIsCurrent() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(owner));
        Mockito.when(bookingRepository
                .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> listOfBookingsActual =
                bookingService.getAllItemBookingsByOwner(owner.getId(), "CURRENT", 0, 10);

        assertThat(listOfBookingsActual.size(), is(1));
        assertThat(listOfBookingsActual.get(0).getId(), is(bookingResponseDto.getId()));
        assertThat(listOfBookingsActual.get(0).getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(listOfBookingsActual.get(0).getItem(), is(bookingResponseDto.getItem()));
        assertThat(listOfBookingsActual.get(0).getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));
        assertThat(listOfBookingsActual.get(0).getEnd(), is(bookingResponseDto.getEnd()));
    }

    @Test
    void testGetAllItemBookingsByOwnerWhenStateIsPast() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(owner));
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> listOfBookingsActual =
                bookingService.getAllItemBookingsByOwner(owner.getId(), "PAST", 0, 10);

        assertThat(listOfBookingsActual.size(), is(1));
        assertThat(listOfBookingsActual.get(0).getId(), is(bookingResponseDto.getId()));
        assertThat(listOfBookingsActual.get(0).getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(listOfBookingsActual.get(0).getItem(), is(bookingResponseDto.getItem()));
        assertThat(listOfBookingsActual.get(0).getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));
        assertThat(listOfBookingsActual.get(0).getEnd(), is(bookingResponseDto.getEnd()));
    }

    @Test
    void testGetAllItemBookingsByOwnerWhenStateIsFuture() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(owner));
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStartAfterOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> listOfBookingsActual =
                bookingService.getAllItemBookingsByOwner(owner.getId(), "FUTURE", 0, 10);

        assertThat(listOfBookingsActual.size(), is(1));
        assertThat(listOfBookingsActual.get(0).getId(), is(bookingResponseDto.getId()));
        assertThat(listOfBookingsActual.get(0).getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(listOfBookingsActual.get(0).getItem(), is(bookingResponseDto.getItem()));
        assertThat(listOfBookingsActual.get(0).getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));
        assertThat(listOfBookingsActual.get(0).getEnd(), is(bookingResponseDto.getEnd()));
    }

    @Test
    void testGetAllItemBookingsByOwnerWhenStateIsWaiting() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(owner));
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> listOfBookingsActual =
                bookingService.getAllItemBookingsByOwner(owner.getId(), "WAITING", 0, 10);

        assertThat(listOfBookingsActual.size(), is(1));
        assertThat(listOfBookingsActual.get(0).getId(), is(bookingResponseDto.getId()));
        assertThat(listOfBookingsActual.get(0).getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(listOfBookingsActual.get(0).getItem(), is(bookingResponseDto.getItem()));
        assertThat(listOfBookingsActual.get(0).getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));
        assertThat(listOfBookingsActual.get(0).getEnd(), is(bookingResponseDto.getEnd()));
    }

    @Test
    void testGetAllItemBookingsByOwnerWhenStateIsRejected() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(owner));
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStatusEqualsOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> listOfBookingsActual =
                bookingService.getAllItemBookingsByOwner(owner.getId(), "REJECTED", 0, 10);

        assertThat(listOfBookingsActual.size(), is(1));
        assertThat(listOfBookingsActual.get(0).getId(), is(bookingResponseDto.getId()));
        assertThat(listOfBookingsActual.get(0).getBooker(), is(bookingResponseDto.getBooker()));
        assertThat(listOfBookingsActual.get(0).getItem(), is(bookingResponseDto.getItem()));
        assertThat(listOfBookingsActual.get(0).getStatus(), is(bookingResponseDto.getStatus()));
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));
        assertThat(listOfBookingsActual.get(0).getEnd(), is(bookingResponseDto.getEnd()));
    }
}
