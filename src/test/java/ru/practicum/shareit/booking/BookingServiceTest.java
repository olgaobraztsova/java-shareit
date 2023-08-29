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
    BookingServiceImpl bookingService;

    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    UserDto bookerDto;
    User booker;
    User owner;
    Item item;
    ItemDto itemDto;
    Booking booking;
    BookingResponseDto bookingResponseDto;
    BookingDto bookingDto;


    @BeforeEach
    void beforeEach() {
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
                .start(LocalDateTime.of(2023, 8, 28, 11, 0))
                .end(LocalDateTime.of(2023, 8, 29, 11, 0))
                .build();

        bookingResponseDto = BookingResponseDto.builder()
                .id(1)
                .item(itemDto)
                .booker(bookerDto)
                .start(LocalDateTime.of(2023, 8, 28, 11, 0))
                .end(LocalDateTime.of(2023, 8, 29, 11, 0))
                .status(Status.WAITING)
                .build();

        booking = Booking.builder()
                .id(1)
                .start(LocalDateTime.of(2023, 8, 28, 11, 0))
                .end(LocalDateTime.of(2023, 8, 29, 11, 0))
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
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));

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
        assertThat(listOfBookingsActual.get(0).getStart(), is(bookingResponseDto.getStart()));

    }

    @Test
    void testGetAllItemBookingsByOwner() {

    }
}
