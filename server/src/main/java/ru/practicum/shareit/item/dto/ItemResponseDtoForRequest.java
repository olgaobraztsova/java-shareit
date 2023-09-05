package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponseDtoForRequest {
    private Integer id;
    private String name;
    private String description;
    private Integer requestId;
    private Boolean available;
}
