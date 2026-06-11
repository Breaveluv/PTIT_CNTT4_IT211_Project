package com.example.prj.service;

import com.example.prj.model.dto.request.UserCreateRequest;
import com.example.prj.model.dto.response.UserResponse;
import com.example.prj.model.entity.Role;
import com.example.prj.model.entity.User;
import com.example.prj.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_Success() {
        UserCreateRequest request = UserCreateRequest.builder()
                .username("testuser")
                .password("password")
                .fullName("Test User")
                .role(Role.STUDENT)
                .build();

        User user = User.builder()
                .id(1L)
                .username("testuser")
                .enabled(true)
                .role(Role.STUDENT)
                .build();

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(user);

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getUserById_Found() {
        User user = User.builder().id(1L).username("test").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(1L);

        assertEquals("test", response.getUsername());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
    }

    @Test
    void deleteUser_Success() {
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByUsername_Mock() {
        User user = User.builder().username("admin").build();
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        
        Optional<User> found = userRepository.findByUsername("admin");
        assertTrue(found.isPresent());
        assertEquals("admin", found.get().getUsername());
    }
}
