package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    public Collection<UserDto> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return UserMapper.usersListToDto(userRepository.findAll());
    }

    @Transactional
    public UserDto getUserById(Integer id) {
        log.info("Получение пользователя по ID {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с ID " + id + " не существует"));
        return UserMapper.userToDto(user);
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        log.info("Создание пользователя с email: {}", user.getEmail());
        return UserMapper.userToDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateUser(UserDto userDto, Integer userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с ID " + userId + " не существует"));

        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            checkIfEmailAlreadyRegistered(userId, userDto.getEmail());
            existingUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            existingUser.setName(userDto.getName());
        }

        log.info("Изменение данных пользователя с ID {}", userId);
        return UserMapper.userToDto(userRepository.save(existingUser));
    }

    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователя с ID " + userId + " не существует"));
        log.info("Удаление пользователя с ID {}", userId);
        userRepository.delete(user);
    }

    private void checkIfEmailAlreadyRegistered(Integer userId, String email) {
        Collection<UserDto> users = getAllUsers();
        for (UserDto u : users) {
            if (u.getId().equals(userId)) {
                continue;
            }
            if (u.getEmail().equals(email)) {
                throw new ConflictExistsException("Email " + email + " уже зарегистрирован");
            }
        }
    }
}
