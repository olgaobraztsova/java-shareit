package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.service.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Integer id;

    @NotBlank(groups = {Create.class}, message = "Имя вещи не может быть пустым")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Описание вещи не может быть пустым")
    private String description;

    @NotNull(groups = {Create.class}, message = "Поле available должно иметь значение")
    private Boolean available;

    private Integer ownerId;

    private Integer requestId;

}
