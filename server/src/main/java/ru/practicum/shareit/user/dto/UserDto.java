package ru.practicum.shareit.user.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer id;

    //@NotBlank(groups = {Create.class}, message = "Имя пользователя не может быть пустым")
    private String name;

    //@NotBlank(groups = {Create.class}, message = "Поле email не может быть пустым")
    //@Email(groups = {Create.class, Update.class}, message = "Введен некорректный email")
    private String email;
}
