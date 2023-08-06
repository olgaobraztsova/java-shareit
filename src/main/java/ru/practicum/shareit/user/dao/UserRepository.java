package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserEmailIdAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserRepository {
    private final Map<Integer, User> userRepository = new HashMap<>();
    private Integer idCounter = 1;

    public User createUser(User user) {
//        checkIfEmailAlreadyRegistered(user.getId(), user.getEmail());
        user.setId(idCounter++);
        userRepository.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
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

//    private void checkIfEmailAlreadyRegistered(Integer userId, String email) {
//        for (User u : userRepository.values()) {
//            if (u.getId().equals(userId)) {
//                continue;
//            }
//            if (u.getEmail().equals(email)) {
//                throw new UserEmailIdAlreadyExistsException("Email " + email + " уже зарегистрирован");
//            }
//        }
//    }
}
