package ru.practicum.shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {

    private final EntityManager em;
    private final ItemRequestService service;

    @Test
    void createItemRequest() {

        User requester = User.builder()
                .name("user requester")
                .email("requester@email.com")
                .build();
        em.persist(requester);

        ItemRequestShortDto itemRequestShortDto = ItemRequestShortDto.builder()
                .description("запрос на вещь")
                .build();

        ItemRequestDto itemRequestDto = service.createItemRequest(1, itemRequestShortDto);

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("id", itemRequestDto.getId()).getSingleResult();


        assertThat(itemRequestDto.getRequester().getId(), is(itemRequest.getRequester().getId()));
        assertThat(itemRequestDto.getRequester().getName(), is(itemRequest.getRequester().getName()));
        assertThat(itemRequestDto.getRequester().getEmail(), is(itemRequest.getRequester().getEmail()));
        assertThat(itemRequestDto.getId(), is(itemRequest.getId()));
        assertThat(itemRequestDto.getDescription(), is(itemRequest.getDescription()));
    }
}
