package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserEmailIdAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserRepository {
    private final Map<Integer, User> userRepository = new HashMap<>();
    private Integer idCounter = 1;

    public User create(User user) {
        checkIfEmailAlreadyRegistered(user);
        user.setId(idCounter++);
        userRepository.put(user.getId(), user);
        return user;
    }

    public User update(User newUser, Integer userId) {
        newUser.setId(userId);
        checkIfUserExists(userId);
        checkIfEmailAlreadyRegistered(newUser);
        User updatedUser = userRepository.get(userId);
        if (newUser.getEmail() != null && !newUser.getEmail().isEmpty()) {
            updatedUser.setEmail(newUser.getEmail());
        }
        if (!newUser.getName().isEmpty()) {
            updatedUser.setName(newUser.getName());
        }
        userRepository.put(updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    public User getUserById(Integer userId) {
        checkIfUserExists(userId);
        return userRepository.get(userId);
    }

    public Collection<User> getAllUsers() {
        return userRepository.values();
    }

    public void delete(Integer userId) {
        checkIfUserExists(userId);
        userRepository.remove(userId);
    }

    private void checkIfUserExists(Integer userId) {
        if (!userRepository.containsKey(userId)) {
            throw new UserNotFoundException("Пользователя с ID " + userId + " не существует");
        }
    }

    private void checkIfEmailAlreadyRegistered(User user) {
        for (User u : userRepository.values()) {
            if (u.getId() == user.getId()) {
                continue;
            }
            if (u.getEmail().equals(user.getEmail())) {
                throw new UserEmailIdAlreadyExistsException("Email " + user.getEmail() + " уже зарегистрирован");
            }
        }
    }
}
