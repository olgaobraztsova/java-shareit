package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

@DataJpaTest
public class ItemRepositoryJpaTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    UserRepository userRepository;

    Item itemAdded;

    @BeforeEach
    void beforeEach() {

        User owner = User.builder()
                .name("owner")
                .email("owner@email.com")
                .build();
        userRepository.save(owner);

        Item item = Item.builder()
                .name("вещь")
                .description("описание вещи")
                .available(true)
                .owner(owner)
                .build();
        itemAdded = itemRepository.save(item);

        Item item2 = Item.builder()
                .name("вещь 2")
                .description("вещь 2")
                .available(true)
                .owner(owner)
                .build();
        itemRepository.save(item2);
    }

    @Test
    void testFindBySearchKey() {
        List<Item> items = itemRepository.findBySearchKey("описание");

        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), itemAdded.getName());
        assertEquals(items.get(0).getOwner(), itemAdded.getOwner());
        assertEquals(items.get(0).getAvailable(), itemAdded.getAvailable());
        assertEquals(items.get(0).getDescription(), itemAdded.getDescription());
    }
}
