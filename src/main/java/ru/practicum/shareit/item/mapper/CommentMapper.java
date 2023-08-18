package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@UtilityClass
public class CommentMapper {

    public CommentResponseDto toCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemDto(ItemMapper.itemToDto(comment.getItem()))
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment toComment(CommentDto commentDto, Item item, User user, LocalDateTime localDateTime) {
        return Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(user)
                .created(localDateTime)
                .build();
    }
    public List<CommentResponseDto> commentsListToDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }

}
