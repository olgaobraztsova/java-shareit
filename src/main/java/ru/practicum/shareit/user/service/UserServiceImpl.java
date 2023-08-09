package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserEmailIdAlreadyExistsException;
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
        checkIfEmailAlreadyRegistered(user.getId(), user.getEmail());
        log.info("Создание пользователя с email: {}", user.getEmail());
        return UserMapper.userToDto(userRepository.createUser(user));
    }

    public UserDto updateUser(UserDto userDto, Integer userId) {
        User existingUser = userRepository.getUserById(userId);

        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            checkIfEmailAlreadyRegistered(userId, userDto.getEmail());
            existingUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            existingUser.setName(userDto.getName());
        }

        log.info("Изменение данных пользователя с ID {}", userId);
        return UserMapper.userToDto(existingUser);
    }

    public void deleteUser(Integer userId) {
        log.info("Добавление пользователя с ID {}", userId);
        userRepository.deleteUser(userId);
    }

    private void checkIfEmailAlreadyRegistered(Integer userId, String email) {
        Collection<UserDto> users = getAllUsers();
        for (UserDto u : users) {
            if (u.getId().equals(userId)) {
                continue;
            }
            if (u.getEmail().equals(email)) {
                throw new UserEmailIdAlreadyExistsException("Email " + email + " уже зарегистрирован");
            }
        }
    }
}
