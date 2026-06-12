package com.example.prj.service;

import com.example.prj.model.entity.PasswordResetToken;
import com.example.prj.model.entity.User;
import com.example.prj.repository.PasswordResetTokenRepository;
import com.example.prj.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void forgotPassword_Success() {
        User user = User.builder().id(1L).username("testuser").build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = authService.forgotPassword("testuser");

        assertNotNull(token);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(tokenRepository, times(1)).save(any(PasswordResetToken.class));
    }

    @Test
    void forgotPassword_UserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.forgotPassword("unknown"));
    }

    @Test
    void resetPassword_Success() {
        User user = User.builder().id(1L).username("testuser").password("oldPass").build();
        PasswordResetToken prt = PasswordResetToken.builder()
                .id(1L)
                .token("validToken")
                .user(user)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        when(tokenRepository.findByToken("validToken")).thenReturn(Optional.of(prt));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tokenRepository.save(any(PasswordResetToken.class))).thenReturn(prt);

        authService.resetPassword("validToken", "newPass");

        assertTrue(prt.isUsed());
        assertEquals("encodedNewPass", user.getPassword());
        verify(userRepository, times(1)).save(user);
        verify(tokenRepository, times(1)).save(prt);
    }

    @Test
    void resetPassword_InvalidToken() {
        when(tokenRepository.findByToken("invalidToken")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.resetPassword("invalidToken", "newPass"));
    }

    @Test
    void resetPassword_ExpiredToken() {
        User user = User.builder().id(1L).username("testuser").build();
        PasswordResetToken prt = PasswordResetToken.builder()
                .id(1L)
                .token("expiredToken")
                .user(user)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .used(false)
                .build();

        when(tokenRepository.findByToken("expiredToken")).thenReturn(Optional.of(prt));

        assertThrows(RuntimeException.class, () -> authService.resetPassword("expiredToken", "newPass"));
    }
}
