package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "where i.id = ?1 " +
            "  and b.start <= ?3 " +
            "  and b.end >= ?2 " +
            "  and b.status in ('WAITING', 'APPROVED')")
    List<Booking> findIfItemIsAvailableForBookingDates(Integer itemId, LocalDateTime start, LocalDateTime end);

    Booking findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(Integer itemId,
                                                                     Status status,
                                                                     LocalDateTime now);

    Booking findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(Integer itemId,
                                                                   Status status,
                                                                   LocalDateTime now);

    Booking findFirstByItemIdAndBookerIdAndStatusAndEndBefore(Integer itemId,
                                                              Integer bookerId,
                                                              Status status,
                                                              LocalDateTime end);

    @Query(
            "SELECT booking " +
                    "FROM Booking AS booking " +
                    "WHERE booking.item IN ?1 AND booking.status = 'APPROVED'"
    )
    List<Booking> findApprovedBookingsFor(Collection<Item> items, Sort sort);

    List<Booking> findByBookerIdOrderByStartDesc(Integer bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer bookerId,
                                                                          LocalDateTime start,
                                                                          LocalDateTime end);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime now);

    List<Booking> findByBookerIdAndStatusEqualsOrderByStartDesc(Integer bookerId, Status status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Integer ownerId);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer ownerId,
                                                                             LocalDateTime start,
                                                                             LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Integer ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusEqualsOrderByStartDesc(Integer ownerId, Status status);
}
