package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemResponseDtoForRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDtoWithResponses {

    private Integer id;
    private String description;
    private UserDto requester;
    private LocalDateTime created;
    private List<ItemResponseDtoForRequest> items;
}
