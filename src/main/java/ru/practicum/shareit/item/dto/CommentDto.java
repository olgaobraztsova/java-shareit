package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CommentDto {
    @NotBlank(message = "Отзыв не может быть пустым")
    private String text;
}
