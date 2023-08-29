package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemDto itemDto;
    private ItemResponseDto itemResponseDto;
    private UserDto owner;
    private CommentDto commentDto;
    private CommentResponseDto commentResponseDto;

    @BeforeEach
    void setup() {
        mapper.registerModule(new JavaTimeModule());

        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        owner = UserDto.builder()
                .id(1)
                .name("user name 1")
                .email("email1@email.com")
                .build();

        itemDto = ItemDto.builder()
                .id(1)
                .name("вещь")
                .description("описание вещи")
                .available(true)
                .ownerId(1)
                .requestId(1)
                .build();

        itemResponseDto = ItemResponseDto.builder()
                .id(2)
                .name("item name")
                .description("item description")
                .owner(owner)
                .available(true)
                .comments(Collections.emptyList())
                .build();

        commentDto = CommentDto.builder()
                .text("comment text")
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .id(1)
                .text("comment text")
                .authorName("user name 1")
                .itemDto(itemDto)
                .created(LocalDateTime.now().minusDays(2))
                .build();
    }

    @Test
    void testCreateItem() throws Exception {
        Mockito.when(itemService.createItem(any(), any())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    void testUpdateItem() throws Exception {
        Mockito.when(itemService.updateItem(any(), any(), any())).thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.ownerId", is(itemDto.getOwnerId())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }


    @Test
    void testGetItemById() throws Exception {
        Mockito.when(itemService.getItemById(any(), any())).thenReturn(itemResponseDto);

        mvc.perform(get("/items/{itemId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription())))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName())))
                .andExpect(jsonPath("$.owner.name", is(itemResponseDto.getOwner().getName())))
                .andExpect(jsonPath("$.comments", is(itemResponseDto.getComments())))
                .andExpect(jsonPath("$.available", is(itemResponseDto.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1)).getItemById(1, 1);
    }

    @Test
    void testPostComments() throws Exception {
        Mockito.when(itemService.postComment(any(), any(), any())).thenReturn(commentResponseDto);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponseDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentResponseDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponseDto.getAuthorName())))
                .andExpect(jsonPath("$.created[0]", is(commentResponseDto.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(commentResponseDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$.created[2]", is(commentResponseDto.getCreated().getDayOfMonth())))
                .andExpect(jsonPath("$.itemDto.name", is(commentResponseDto.getItemDto().getName())))
                .andExpect(jsonPath("$.itemDto.description", is(commentResponseDto.getItemDto().getDescription())));

        Mockito.verify(itemService, Mockito.times(1)).postComment(any(), any(), any());
    }
}
