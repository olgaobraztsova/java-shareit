package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Item {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
}
