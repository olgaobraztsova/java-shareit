package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class BookingRepositoryJpaTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    private Item itemFromDb;
    private User bookerFromDb;


    @BeforeEach
    void beforeEach() {
        User booker = User.builder()
                .name("user name 1")
                .email("email1@email.com")
                .build();
        bookerFromDb = userRepository.save(booker);

        User owner = User.builder()
                .name("owner")
                .email("owner@email.com")
                .build();
        userRepository.save(owner);

        Item item = Item.builder()
                .name("вещь")
                .description("описание вещи")
                .available(true)
                .owner(owner)
                .build();
        itemFromDb = itemRepository.save(item);

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .status(Status.APPROVED)
                .item(item)
                .booker(booker)
                .build();
        bookingRepository.save(booking);
    }

    @Test
    void testFindIfItemIsAvailableForBookingDates() {
        List<Booking> bookings = bookingRepository
                .findIfItemIsAvailableForBookingDates(
                        itemFromDb.getId(),
                        LocalDateTime.now().plusDays(2).plusHours(2),
                        LocalDateTime.now().plusDays(3).minusHours(2));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getName(), itemFromDb.getName());
        assertEquals(bookings.get(0).getBooker().getId(), bookerFromDb.getId());
        assertEquals(bookings.get(0).getBooker().getEmail(), bookerFromDb.getEmail());
    }

    @Test
    void findApprovedBookingsFor() {
        List<Booking> bookings = bookingRepository
                .findApprovedBookingsFor(List.of(itemFromDb), Sort.by(Sort.Direction.DESC, "start"));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getName(), itemFromDb.getName());
        assertEquals(bookings.get(0).getItem().getOwner(), itemFromDb.getOwner());
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }
}
