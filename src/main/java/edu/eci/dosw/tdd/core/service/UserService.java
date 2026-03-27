package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.IdGeneratorUtil;
import edu.eci.dosw.tdd.core.Validator.UserValidator;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.mapper.UserPersistenceMapper;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserValidator userValidator;
    private final UserRepository userRepository;
    private final UserPersistenceMapper userMapper;

    public User registerUser(User user) {
        userValidator.validate(user);
        if (user.getId() == null) {
            user.setId(IdGeneratorUtil.generateId());
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con el username: " + user.getUsername());
        }
        UserEntity saved = userRepository.save(userMapper.toEntity(user));
        return userMapper.toDomain(saved);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id)
                .map(userMapper::toDomain);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }
}