package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    User user;
    UserDto userDto;
    User updatedUser;
    UserDto updatedUserDto;

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(userRepository);

        user = new User(1, "user name", "user@email.com");
        userDto = new UserDto(1, "user name", "user@email.com");

        updatedUser = new User(1, "user name updated", "user@email.com");
        updatedUserDto = new UserDto(1, "user name updated", "user@email.com");
    }

    @Test
    void testCreateUser() {
        Mockito.when(userRepository.save(any())).thenReturn(user);

        UserDto actualUser = userService.createUser(userDto);

        assertThat(actualUser.getEmail(), is(userDto.getEmail()));
        assertThat(actualUser.getName(), is(userDto.getName()));
        assertThat(actualUser.getId(), is(userDto.getId()));
    }

    @Test
    void testUpdateUser() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(any())).thenReturn(updatedUser);

        UserDto actualUser = userService.updateUser(updatedUserDto, updatedUser.getId());

        assertThat(actualUser.getEmail(), is(updatedUserDto.getEmail()));
        assertThat(actualUser.getName(), is(updatedUserDto.getName()));
        assertThat(actualUser.getId(), is(updatedUserDto.getId()));

    }

    @Test
    void testGetUserById() {
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.ofNullable(user));

        UserDto actualUser = userService.getUserById(user.getId());

        assertThat(actualUser.getEmail(), is(userDto.getEmail()));
        assertThat(actualUser.getName(), is(userDto.getName()));
        assertThat(actualUser.getId(), is(userDto.getId()));
    }

    @Test
    void testGetUserByIdWhenUserDoesNotExist() {
        Mockito.when(userRepository.findById(any()))
                .thenThrow(new EntityNotFoundException("Пользователя с ID " + 5 + " не существует"));

        final EntityNotFoundException exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.getUserById(5));

        Assertions.assertEquals("404 NOT_FOUND \"Пользователя с ID 5 не существует\"",
                exception.getMessage());
    }
}
