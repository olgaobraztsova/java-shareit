package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {

    private final EntityManager em;
    private final ItemService service;

    @Test
    void getUserItems() {
        Integer from = 0;
        Integer size = 10;

        User owner = User.builder()
                .name("user owner")
                .email("owner@email.com")
                .build();
        em.persist(owner);

        User requester = User.builder()
                .name("user requester")
                .email("requester@email.com")
                .build();
        em.persist(requester);

        User booker = User.builder()
                .name("user booker")
                .email("booker@email.com")
                .build();
        em.persist(booker);

        ItemRequest itemRequest = ItemRequest.builder()
                .description("запрос на вещь")
                .created(LocalDateTime.of(2023, 8, 20, 11, 0))
                .requester(requester)
                .build();
        em.persist(itemRequest);

        Item item = Item.builder()
                .name("вещь")
                .description("описание вещи")
                .owner(owner)
                .available(true)
                .request(itemRequest)
                .build();
        em.persist(item);

        Item item2 = Item.builder()
                .name("вещь 2")
                .description("описание вещи 2")
                .owner(owner)
                .available(true)
                .build();
        em.persist(item2);

        Booking booking = Booking.builder()
                .item(item)
                .start(LocalDateTime.of(2023, 8, 21, 11, 0))
                .end(LocalDateTime.of(2023, 8, 22, 11, 0))
                .status(Status.APPROVED)
                .booker(booker)
                .build();
        em.persist(booking);

        Comment comment = Comment.builder()
                .item(item)
                .author(booker)
                .created(LocalDateTime.of(2023, 8, 24, 11, 0))
                .text("комментарий по вещи")
                .build();
        em.persist(comment);

        em.flush();

        Collection<ItemResponseDto> itemResponseDtoActual = service.getUserItems(owner.getId(), from, size);

        assertThat(itemResponseDtoActual.size(), is(2));

        ArrayList<ItemResponseDto> itemResponseDtoActualArrayList = new ArrayList<>(itemResponseDtoActual);

        assertThat(itemResponseDtoActualArrayList.get(0).getId(), is(item.getId()));
        assertThat(itemResponseDtoActualArrayList.get(0).getName(), is(item.getName()));
        assertThat(itemResponseDtoActualArrayList.get(0).getDescription(), is(item.getDescription()));
        assertThat(itemResponseDtoActualArrayList.get(0).getAvailable(), is(item.getAvailable()));
        assertThat(itemResponseDtoActualArrayList.get(0).getOwner().getName(), is(item.getOwner().getName()));

        assertThat(itemResponseDtoActualArrayList.get(0).getLastBooking().getStart(), is(booking.getStart()));
        assertThat(itemResponseDtoActualArrayList.get(0).getLastBooking().getEnd(), is(booking.getEnd()));
        assertThat(itemResponseDtoActualArrayList.get(0).getLastBooking().getBookerId(), is(booking.getBooker().getId()));

        assertThat(itemResponseDtoActualArrayList.get(0).getComments().size(), is(1));
        ArrayList<CommentResponseDto> commentsList = new ArrayList<>(itemResponseDtoActualArrayList.get(0).getComments());
        assertThat(commentsList.get(0).getText(), is(comment.getText()));
        assertThat(commentsList.get(0).getAuthorName(), is(comment.getAuthor().getName()));
        assertThat(commentsList.get(0).getItemDto().getId(), is(comment.getItem().getId()));
        assertThat(commentsList.get(0).getCreated(), is(comment.getCreated()));

        assertThat(itemResponseDtoActualArrayList.get(1).getId(), is(item2.getId()));
        assertThat(itemResponseDtoActualArrayList.get(1).getName(), is(item2.getName()));
        assertThat(itemResponseDtoActualArrayList.get(1).getDescription(), is(item2.getDescription()));
        assertThat(itemResponseDtoActualArrayList.get(1).getAvailable(), is(item2.getAvailable()));
        assertThat(itemResponseDtoActualArrayList.get(1).getOwner().getName(), is(item2.getOwner().getName()));
    }
}
