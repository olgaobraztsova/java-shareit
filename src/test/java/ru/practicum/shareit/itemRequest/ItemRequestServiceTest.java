package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemResponseDtoForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithResponses;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;


    private User owner;
    private User requester;
    private UserDto ownerDto;
    private UserDto requesterDto;
    private ItemRequestShortDto itemRequestShortDto;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDtoExpected;
    private ItemRequestDtoWithResponses itemRequestDtoWithResponsesExpected;
    private Item item;

    @BeforeEach
    void beforeEach() {

        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRequestRepository, itemRepository);

        owner = new User(1, "user name 1", "email1@email.com");
        requester = new User(2, "user name 2", "email2@email.com");
        ownerDto = new UserDto(1, "user name 1", "email1@email.com");
        requesterDto = new UserDto(2, "user name 2", "email2@email.com");

        itemRequest = new ItemRequest(
                1,
                "Описание запроса",
                requester,
                LocalDateTime.of(2023, 8, 26, 11, 0, 0));
        itemRequestDtoExpected = new ItemRequestDto(1,
                "Описание запроса",
                requester,
                LocalDateTime.of(2023, 8, 26, 11, 0, 0));
        itemRequestShortDto = new ItemRequestShortDto("Описание запроса");

        item = Item.builder()
                .id(1)
                .name("вещь")
                .description("вещь в ответ на описание запроса")
                .request(itemRequest)
                .available(true)
                .owner(owner)
                .build();

        ItemResponseDtoForRequest itemResponseDtoForRequest = ItemResponseDtoForRequest.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .requestId(item.getRequest().getId())
                .available(item.getAvailable())
                .build();

        itemRequestDtoWithResponsesExpected =
                ItemRequestDtoWithResponses.builder()
                        .id(itemRequestDtoExpected.getId())
                        .description(itemRequestDtoExpected.getDescription())
                        .created(itemRequestDtoExpected.getCreated())
                        .requester(requesterDto)
                        .items(List.of(itemResponseDtoForRequest))
                        .build();

    }

    @Test
    void testCreateItemRequest() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(requester));
        Mockito.when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto itemRequestDtoActual = itemRequestService.createItemRequest(requester.getId(), itemRequestShortDto);

        assertThat(itemRequestDtoActual.getId(), is(itemRequestDtoExpected.getId()));
        assertThat(itemRequestDtoActual.getDescription(), is(itemRequestDtoExpected.getDescription()));
        assertThat(itemRequestDtoActual.getRequester().getId(), is(itemRequestDtoExpected.getRequester().getId()));
        assertThat(itemRequestDtoActual.getCreated(), is(itemRequestDtoExpected.getCreated()));
    }

    @Test
    void testGetItemRequestsByUserId() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(requester));
        Mockito.when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(any())).thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findAllByRequestIdIn(any())).thenReturn(List.of(item));

        List<ItemRequestDtoWithResponses> itemRequestDtoWithResponsesActual =
                itemRequestService.getItemRequestsByUserId(requester.getId());

        assertThat(itemRequestDtoWithResponsesActual.size(),
                is(List.of(itemRequestDtoWithResponsesExpected).size()));
        assertThat(itemRequestDtoWithResponsesActual.get(0).getId(),
                is(List.of(itemRequestDtoWithResponsesExpected).get(0).getId()));
        assertThat(itemRequestDtoWithResponsesActual.get(0).getDescription(),
                is(List.of(itemRequestDtoWithResponsesExpected).get(0).getDescription()));
        assertThat(itemRequestDtoWithResponsesActual.get(0).getItems().size(),
                is(List.of(itemRequestDtoWithResponsesExpected).get(0).getItems().size()));
        assertThat(itemRequestDtoWithResponsesActual.get(0).getRequester(),
                is(List.of(itemRequestDtoWithResponsesExpected).get(0).getRequester()));
        assertThat(itemRequestDtoWithResponsesActual.get(0).getItems().get(0).getName(),
                is(List.of(itemRequestDtoWithResponsesExpected).get(0).getItems().get(0).getName()));

    }

    @Test
    void testGetItemRequestsByOtherUsers() {

        Mockito.when(itemRequestRepository.findAllByRequesterIdNot(any(), any())).thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findAllByRequestIdIn(any())).thenReturn(List.of(item));

        List<ItemRequestDtoWithResponses> itemRequestDtoWithResponsesActual =
                itemRequestService.getItemRequestsByOtherUsers(owner.getId(), 0, 10);

        assertThat(itemRequestDtoWithResponsesActual.size(),
                is(List.of(itemRequestDtoWithResponsesExpected).size()));
        assertThat(itemRequestDtoWithResponsesActual.get(0).getId(),
                is(List.of(itemRequestDtoWithResponsesExpected).get(0).getId()));
        assertThat(itemRequestDtoWithResponsesActual.get(0).getDescription(),
                is(List.of(itemRequestDtoWithResponsesExpected).get(0).getDescription()));
        assertThat(itemRequestDtoWithResponsesActual.get(0).getItems().size(),
                is(List.of(itemRequestDtoWithResponsesExpected).get(0).getItems().size()));
        assertThat(itemRequestDtoWithResponsesActual.get(0).getRequester(),
                is(List.of(itemRequestDtoWithResponsesExpected).get(0).getRequester()));
        assertThat(itemRequestDtoWithResponsesActual.get(0).getItems().get(0).getName(),
                is(List.of(itemRequestDtoWithResponsesExpected).get(0).getItems().get(0).getName()));
        assertThat(itemRequestDtoWithResponsesActual.get(0).getItems().get(0).getDescription(),
                is(List.of(itemRequestDtoWithResponsesExpected).get(0).getItems().get(0).getDescription()));
        assertThat(itemRequestDtoWithResponsesActual.get(0).getItems().get(0).getAvailable(),
                is(List.of(itemRequestDtoWithResponsesExpected).get(0).getItems().get(0).getAvailable()));
        assertThat(itemRequestDtoWithResponsesActual.get(0).getItems().get(0).getId(),
                is(List.of(itemRequestDtoWithResponsesExpected).get(0).getItems().get(0).getId()));
    }

    @Test
    void testGetItemRequestById() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(requester));
        Mockito.when(itemRequestRepository.findById(any())).thenReturn(Optional.ofNullable(itemRequest));

        Mockito.when(itemRepository.findAllByRequestIdIn(any())).thenReturn(List.of(item));

        ItemRequestDtoWithResponses itemRequestDtoWithResponsesActual =
                itemRequestService.getItemRequestById(itemRequest.getId(), requester.getId());

        assertThat(itemRequestDtoWithResponsesActual.getId(),
                is(itemRequestDtoWithResponsesExpected.getId()));
        assertThat(itemRequestDtoWithResponsesActual.getDescription(),
                is(itemRequestDtoWithResponsesExpected.getDescription()));
        assertThat(itemRequestDtoWithResponsesActual.getItems().size(),
                is(itemRequestDtoWithResponsesExpected.getItems().size()));
        assertThat(itemRequestDtoWithResponsesActual.getRequester(),
                is(itemRequestDtoWithResponsesExpected.getRequester()));
        assertThat(itemRequestDtoWithResponsesActual.getItems().get(0).getName(),
                is(itemRequestDtoWithResponsesExpected.getItems().get(0).getName()));
    }
}
