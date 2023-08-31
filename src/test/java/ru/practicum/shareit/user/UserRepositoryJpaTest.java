package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UserRepositoryJpaTest {

    @Autowired
    UserRepository userRepository;

    User user;

    @BeforeEach
    void beforeEach() {

        user = User.builder()
                .id(1)
                .name("owner")
                .email("owner@email.com")
                .build();
        userRepository.save(user);
    }

    @Test
    void testDeleteUser() {
        userRepository.delete(user);
        List<User> users = userRepository.findAll();

        assertEquals(0, users.size());
    }
}
