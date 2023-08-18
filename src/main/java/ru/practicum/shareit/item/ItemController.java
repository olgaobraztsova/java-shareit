package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.booking.service.validation.Create;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@Validated(Create.class)
                              @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer itemId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto getItemById(@PathVariable Integer itemId,
                                           @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemResponseDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findItems(@RequestParam(name = "text") String text) {
        return itemService.findItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto postComment(@Valid @RequestBody CommentDto commentDto,
                                          @RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @PathVariable Integer itemId) {

        return itemService.postComment(commentDto, userId, itemId);
    }

}
