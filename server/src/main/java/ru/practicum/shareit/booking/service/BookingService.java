package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(Integer bookerId, BookingDto bookingDto);

    BookingResponseDto getBookingById(Integer userId, Integer bookingId);

    BookingResponseDto approveBooking(Integer bookerId, Integer bookingId, Boolean isApproved);

    List<BookingResponseDto> getAllBookingsByUser(Integer bookerId, State state, Integer from, Integer size);

    List<BookingResponseDto> getAllItemBookingsByOwner(Integer ownerId, State state, Integer from, Integer size);

}