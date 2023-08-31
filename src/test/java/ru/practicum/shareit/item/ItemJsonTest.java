package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDtoForRequest;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemJsonTest {

    @Autowired
    private JacksonTester<CommentResponseDto> json;

    @Autowired
    private JacksonTester<ItemResponseDtoForRequest> jsonItemResponse;

    @Test
    void testItemDto() throws IOException {

        LocalDateTime date = LocalDateTime.of(2023, 8, 28, 11, 0);

        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("item name")
                .description("item description")
                .available(true)
                .ownerId(1)
                .build();

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1)
                .created(date)
                .authorName("author")
                .text("comment text")
                .itemDto(itemDto)
                .build();

        JsonContent<CommentResponseDto> jsonContent = json.write(commentResponseDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(commentResponseDto.getId());
        assertThat(jsonContent).extractingJsonPathStringValue("$.authorName").isEqualTo(commentResponseDto.getAuthorName());
        assertThat(jsonContent).extractingJsonPathStringValue("$.text").isEqualTo(commentResponseDto.getText());
        assertThat(jsonContent).extractingJsonPathStringValue("$.itemDto.name")
                .isEqualTo(commentResponseDto.getItemDto().getName());
        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemDto.id")
                .isEqualTo(commentResponseDto.getItemDto().getId());
        assertThat(jsonContent).extractingJsonPathStringValue("$.itemDto.description")
                .isEqualTo(commentResponseDto.getItemDto().getDescription());
        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemDto.ownerId")
                .isEqualTo(commentResponseDto.getItemDto().getOwnerId());
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.itemDto.available")
                .isEqualTo(commentResponseDto.getItemDto().getAvailable());
    }

    @Test
    void testItemRequestDto() throws IOException {

        ItemResponseDtoForRequest itemResponseDtoForRequest = ItemResponseDtoForRequest.builder()
                .id(1)
                .description("description")
                .requestId(1)
                .name("item name")
                .available(true)
                .build();

        JsonContent<ItemResponseDtoForRequest> jsonContent = jsonItemResponse.write(itemResponseDtoForRequest);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(itemResponseDtoForRequest.getId());
        assertThat(jsonContent).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemResponseDtoForRequest.getDescription());
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo(itemResponseDtoForRequest.getName());
        assertThat(jsonContent).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemResponseDtoForRequest.getRequestId());
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemResponseDtoForRequest.getAvailable());
    }
}
