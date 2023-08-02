package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public Collection<UserDto> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return UserMapper.usersListToDto(userRepository.getAllUsers());
    }

    public UserDto getUserById(Integer id) {
        log.info("Получение пользователя по ID {}", id);
        return UserMapper.userToDto(userRepository.getUserById(id));
    }

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        log.info("Создание пользователя с email: {}", user.getEmail());
        return UserMapper.userToDto(userRepository.createUser(user));
    }

    public UserDto updateUser(UserDto userDto, Integer userId) {
        log.info("Изменение данных пользователя с ID {}", userId);
        return UserMapper.userToDto(userRepository.updateUser(userDto, userId));
    }

    public void deleteUser(Integer userId) {
        log.info("Добавление пользователя с ID {}", userId);
        userRepository.deleteUser(userId);
    }
}
