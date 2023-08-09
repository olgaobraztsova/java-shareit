package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@ToString
public class ItemDto {
    private Integer id;

    @NotBlank(groups = {Create.class}, message = "Имя вещи не может быть пустым")
    private String name;

    @NotBlank(groups = {Create.class}, message = "Описание вещи не может быть пустым")
    private String description;

    @NotNull(groups = {Create.class}, message = "Поле available должно иметь значение")
    private Boolean available;
}
