package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    @Override
    public BookingResponseDto createBooking(Integer bookerId, BookingDto bookingDto) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с ID " + bookerId + " не существует"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("Вещь с ID " + bookingDto.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            throw new AvailabilityException("Вещь с ID " + item.getId() + " недоступна для бронирования");
        }

        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new AvailabilityException("Дата окончания бронирования не может быть раньше или равна дате начала");
        }

        if (item.getOwner().getId().equals(bookerId)) {
            throw new EntityNotFoundException("Владелец вещи не может бронировать собственную вещь");
        }

        if (bookingRepository.findIfItemIsAvailableForBookingDates(
                item.getId(), bookingDto.getStart(), bookingDto.getEnd()).size() > 0) {
            throw new AvailabilityException("Вещь с ID " + item.getId() + " уже забронирована на указанные даты");
        }
        ;

        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);

        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingResponseDto getBookingById(Integer userId, Integer bookingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с ID " + userId + " не существует"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирования с ID " + bookingId + " не существует"));

        Integer bookerId = booking.getBooker().getId();
        Integer ownerId = booking.getItem().getOwner().getId();
        if (!bookerId.equals(userId) && !ownerId.equals(userId)) {
            throw new EntityNotFoundException("Доступ к бронированию возможен только владельцем вещи или пользователем, " +
                    "совершившим данное бронирование");
        }
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto approveBooking(Integer userId, Integer bookingId, Boolean isApproved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с ID " + userId + " не существует"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Бронирования с ID " + bookingId + " не существует"));

        Integer ownerId = booking.getItem().getOwner().getId();

        if (!ownerId.equals(userId)) {
            throw new EntityNotFoundException("Пользователь с ID " + userId + " не является владельцем вещи " +
                    booking.getItem().getName() + " и не может одобрить или отклонить ее бронирование.");
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new AvailabilityException("Бронирование с id = " + bookingId + " не ожидает подтверждения");
        }

        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        Booking finalBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingResponseDto(finalBooking);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByUser(Integer bookerId, String state) {
        User user = userRepository.findById(bookerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с ID " + bookerId + " не существует"));

        State bookingState = State.getEnumValue(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> userBookings = Collections.emptyList();

        switch (bookingState) {
            case ALL:
                userBookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
                log.info("Вывод всех бронирований пользователя {}, (ALL)", bookerId);
                break;
            case CURRENT:
                userBookings = bookingRepository
                        .findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, now, now);
                log.info("Вывод текущих бронирований пользователя {}, (CURRENT)", bookerId);
                break;
            case PAST:
                userBookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now);
                log.info("Вывод прошлых бронирований пользователя {}, (PAST)", bookerId);
                break;
            case FUTURE:
                userBookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(bookerId, now);
                log.info("Вывод будущих бронирований пользователя {}, (FUTURE)", bookerId);
                break;
            case WAITING:
                userBookings = bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(bookerId, Status.WAITING);
                log.info("Вывод всех бронирований пользователя {} со статусом WAITING", bookerId);
                break;
            case REJECTED:
                userBookings = bookingRepository.findByBookerIdAndStatusEqualsOrderByStartDesc(bookerId, Status.REJECTED);
                log.info("Вывод всех бронирований пользователя {} со статусом REJECTED", bookerId);
                break;
            default:
                throw new IllegalArgumentException("Неизвестное значение параметра state");
        }
        return BookingMapper.bookingsListToDto(userBookings);
    }

    @Override
    public List<BookingResponseDto> getAllItemBookingsByOwner(Integer ownerId, String state) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с ID " + ownerId + " не существует"));

        if (itemRepository.findAllByOwnerId(ownerId).size() == 0) {
            throw new EntityNotFoundException("Пользователь c ID " + ownerId + " не является владельцем ни одной вещи");
        }

        State bookingState = State.getEnumValue(state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> ownerBookings;

        switch (bookingState) {
            case ALL:
                ownerBookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                log.info("Вывод всех бронирований вещей пользователя {}, (ALL)", ownerId);
                break;
            case CURRENT:
                ownerBookings = bookingRepository
                        .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
                log.info("Вывод текущих бронирований пользователя {}, (CURRENT)", ownerId);
                break;
            case PAST:
                ownerBookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
                log.info("Вывод прошлых бронирований пользователя {}, (PAST)", ownerId);
                break;
            case FUTURE:
                ownerBookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
                log.info("Вывод будущих бронирований пользователя {}, (FUTURE)", ownerId);
                break;
            case WAITING:
                ownerBookings = bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(ownerId, Status.WAITING);
                log.info("Вывод всех бронирований пользователя {} со статусом WAITING", ownerId);
                break;
            case REJECTED:
                ownerBookings = bookingRepository.findByItemOwnerIdAndStatusEqualsOrderByStartDesc(ownerId, Status.REJECTED);
                log.info("Вывод всех бронирований пользователя {} со статусом REJECTED", ownerId);
                break;
            default:
                throw new IllegalArgumentException("Неизвестное значение параметра state");
        }
        return BookingMapper.bookingsListToDto(ownerBookings);
    }
}
