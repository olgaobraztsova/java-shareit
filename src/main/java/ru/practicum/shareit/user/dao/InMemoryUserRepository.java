package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryUserRepository {
    private final Map<Integer, User> userRepository = new HashMap<>();
    private Integer idCounter = 1;

    public User createUser(User user) {
        user.setId(idCounter++);
        userRepository.put(user.getId(), user);
        return user;
    }

    public User getUserById(Integer userId) {
        checkIfUserExists(userId);
        return userRepository.get(userId);
    }

    public Collection<User> getAllUsers() {
        return userRepository.values();
    }

    public void deleteUser(Integer userId) {
        checkIfUserExists(userId);
        userRepository.remove(userId);
    }

    private void checkIfUserExists(Integer userId) {
        if (!userRepository.containsKey(userId)) {
            throw new EntityNotFoundException("Пользователя с ID " + userId + " не существует");
        }
    }
}
