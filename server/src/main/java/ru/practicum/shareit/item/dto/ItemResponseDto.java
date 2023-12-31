package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.user.dto.UserDto;


import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponseDto implements Comparable<ItemResponseDto> {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;

    @Override
    public int compareTo(ItemResponseDto o) {
        return this.getId() - o.getId();
    }

    private Collection<CommentResponseDto> comments;
}