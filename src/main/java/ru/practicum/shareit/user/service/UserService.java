package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> getAllUsers();

    UserDto getUserById(Integer id);

    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Integer userId);

    void delete(Integer userId);
}
