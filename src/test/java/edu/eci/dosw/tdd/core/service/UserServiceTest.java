package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.Validator.UserValidator;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.mapper.UserPersistenceMapper;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserValidator userValidator;
    @Mock private UserRepository userRepository;
    @Mock private UserPersistenceMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User sampleUser;
    private UserEntity sampleEntity;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .name("Test User")
                .username("testuser")
                .password("plainpass")
                .role("USER")
                .build();

        sampleEntity = UserEntity.builder()
                .id("u1")
                .name("Test User")
                .username("testuser")
                .password("encodedpass")
                .role(UserEntity.Role.USER)
                .build();
    }

    @Test
    void registerUser_ShouldRegisterSuccessfully() {
        doNothing().when(userValidator).validate(any());
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedpass");
        when(userMapper.toEntity(any())).thenReturn(sampleEntity);
        when(userRepository.save(any())).thenReturn(sampleEntity);
        when(userMapper.toDomain(sampleEntity)).thenReturn(
                User.builder().id("u1").name("Test User").username("testuser").role("USER").build()
        );

        User result = userService.registerUser(sampleUser);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Test User", result.getName());
        verify(userRepository, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        doNothing().when(userValidator).validate(any());
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(sampleUser));

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_ShouldThrowException_WhenValidatorFails() {
        doThrow(new IllegalArgumentException("El nombre del usuario no puede estar vacio."))
                .when(userValidator).validate(any());

        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(User.builder().name("").build()));

        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        User domainUser = User.builder().id("u1").name("Test User").build();
        when(userRepository.findAll()).thenReturn(List.of(sampleEntity));
        when(userMapper.toDomain(sampleEntity)).thenReturn(domainUser);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        User domainUser = User.builder().id("u1").name("Test User").build();
        when(userRepository.findById("u1")).thenReturn(Optional.of(sampleEntity));
        when(userMapper.toDomain(sampleEntity)).thenReturn(domainUser);

        Optional<User> result = userService.getUserById("u1");

        assertTrue(result.isPresent());
        assertEquals("u1", result.get().getId());
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenNotExists() {
        when(userRepository.findById("id-inexistente")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById("id-inexistente");

        assertFalse(result.isPresent());
    }

    @Test
    void getUserByUsername_ShouldReturnUser_WhenExists() {
        User domainUser = User.builder().id("u1").username("testuser").build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleEntity));
        when(userMapper.toDomain(sampleEntity)).thenReturn(domainUser);

        Optional<User> result = userService.getUserByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }
}