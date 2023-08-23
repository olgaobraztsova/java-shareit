package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Getter
@AllArgsConstructor
public class ItemWithBookingsAndComments {
    private Item item;
    private List<Booking> bookings;
    private List<Comment> comments;
}
