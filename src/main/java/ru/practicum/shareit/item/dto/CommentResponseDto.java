package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class CommentResponseDto {
    private Integer id;
    private String text;
    private ItemDto itemDto;
    private String authorName;
    private LocalDateTime created;

}
