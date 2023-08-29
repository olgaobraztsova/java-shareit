package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(Integer bookerId, BookingDto bookingDto);

    BookingResponseDto getBookingById(Integer userId, Integer bookingId);

    BookingResponseDto approveBooking(Integer bookerId, Integer bookingId, Boolean isApproved);

    List<BookingResponseDto> getAllBookingsByUser(Integer bookerId, String state, Integer from, Integer size);

    List<BookingResponseDto> getAllItemBookingsByOwner(Integer ownerId, String state, Integer from, Integer size);

}