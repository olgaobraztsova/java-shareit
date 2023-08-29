package ru.practicum.shareit.itemRequest;

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
import ru.practicum.shareit.item.dto.ItemResponseDtoForRequest;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private ItemRequestDtoWithResponses itemRequestDtoWithResponses;
    private ItemRequestDtoWithResponses itemRequestDtoWithResponses2;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setup() {
        mapper.registerModule(new JavaTimeModule());

        mvc = MockMvcBuilders
                .standaloneSetup(itemRequestController)
                .build();

        ItemResponseDtoForRequest item = ItemResponseDtoForRequest.builder()
                .id(1)
                .name("item name")
                .description("item description")
                .available(true)
                .requestId(1)
                .build();

        itemRequestDtoWithResponses = ItemRequestDtoWithResponses.builder()
                .id(1)
                .items(List.of(item))
                .description("description")
                .requester(new UserDto(1, "username", "user@email.com"))
                .created(LocalDateTime.now())
                .build();

        itemRequestDtoWithResponses2 = ItemRequestDtoWithResponses.builder()
                .id(2)
                .description("description 2")
                .requester(new UserDto(2, "username 2", "user2@email.com"))
                .created(LocalDateTime.now())
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1)
                .description("description")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateItemRequest() throws Exception {
        Mockito.when(itemRequestService.createItemRequest(any(), any())).thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created[0]", is(itemRequestDto.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(itemRequestDto.getCreated().getMonthValue())))
                .andExpect(jsonPath("$.created[2]", is(itemRequestDto.getCreated().getDayOfMonth())));
    }

    @Test
    void testGetItemRequestsByUserId() throws Exception {

        Mockito.when(itemRequestService.getItemRequestsByUserId(any()))
                .thenReturn(List.of(itemRequestDtoWithResponses));

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoWithResponses.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoWithResponses.getDescription())))
                .andExpect(jsonPath("$[0].created[0]", is(itemRequestDtoWithResponses.getCreated().getYear())))
                .andExpect(jsonPath("$[0].created[1]", is(itemRequestDtoWithResponses.getCreated().getMonthValue())))
                .andExpect(jsonPath("$[0].created[2]", is(itemRequestDtoWithResponses.getCreated().getDayOfMonth())));

        Mockito.verify(itemRequestService, Mockito.times(1)).getItemRequestsByUserId(1);
    }

    @Test
    void testGetItemRequestsByOtherUsers() throws Exception {
        Mockito.when(itemRequestService.getItemRequestsByOtherUsers(any(), any(), any()))
                .thenReturn(List.of(itemRequestDtoWithResponses2));

        mvc.perform(get("/requests/all")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoWithResponses2.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoWithResponses2.getDescription())))
                .andExpect(jsonPath("$[0].created[0]", is(itemRequestDtoWithResponses2.getCreated().getYear())))
                .andExpect(jsonPath("$[0].created[1]", is(itemRequestDtoWithResponses2.getCreated().getMonthValue())))
                .andExpect(jsonPath("$[0].created[2]", is(itemRequestDtoWithResponses2.getCreated().getDayOfMonth())));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getItemRequestsByOtherUsers(1, 0, 10);
    }

    @Test
    void testGetItemRequestById() throws Exception {

        Mockito.when(itemRequestService.getItemRequestById(any(), any()))
                .thenReturn(itemRequestDtoWithResponses);

        mvc.perform(get("/requests/{requestId}", itemRequestDtoWithResponses.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoWithResponses.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoWithResponses.getDescription())))
                .andExpect(jsonPath("$.created[0]", is(itemRequestDtoWithResponses.getCreated().getYear())))
                .andExpect(jsonPath("$.created[1]", is(itemRequestDtoWithResponses.getCreated().getMonthValue())))
                .andExpect(jsonPath("$.created[2]", is(itemRequestDtoWithResponses.getCreated().getDayOfMonth())));

        Mockito.verify(itemRequestService, Mockito.times(1)).getItemRequestById(1, 1);
    }
}
