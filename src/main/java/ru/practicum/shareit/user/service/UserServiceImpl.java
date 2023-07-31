package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserInputMissingOrInvalidEmailException;
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
        log.debug("Получение списка всех пользователей");
        return UserMapper.usersListToDto(userRepository.getAllUsers());
    }

    public UserDto getUserById(Integer id) {
        log.debug("Получение пользователя по ID " + id);
        return UserMapper.UserToDto(userRepository.getUserById(id));
    }

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validateEmail(user);
        log.debug("Создание пользователя с email: " + user.getEmail());
        return UserMapper.UserToDto(userRepository.create(user));
    }

    public UserDto update(UserDto userDto, Integer userId) {
        User user = UserMapper.toUser(getUserById(userId));
        if (userDto.getName() == null) {
            userDto.setName(user.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(user.getEmail());
        }
        validateEmail(UserMapper.toUser(userDto));
        log.debug("Изменение данных пользователя с ID " + userId);
        return UserMapper.UserToDto(userRepository.update(UserMapper.toUser(userDto), userId));
    }

    public void delete(Integer userId) {
        userRepository.delete(userId);
    }

    private void validateEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new UserInputMissingOrInvalidEmailException("Email не может быть пустым");
        }
    }
}
