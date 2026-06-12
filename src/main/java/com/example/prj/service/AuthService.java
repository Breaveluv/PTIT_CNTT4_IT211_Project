package com.example.prj.service;

import com.example.prj.model.dto.request.AuthRequest;
import com.example.prj.model.dto.response.AuthResponse;
import com.example.prj.model.dto.request.RegisterRequest;
import com.example.prj.model.entity.Role;
import com.example.prj.model.entity.TokenBlacklist;
import com.example.prj.model.entity.User;
import com.example.prj.model.entity.PasswordResetToken; // Import mới
import com.example.prj.repository.PasswordResetTokenRepository; // Import mới
import com.example.prj.repository.TokenBlacklistRepository;
import com.example.prj.repository.UserRepository;
import com.example.prj.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import mới

import java.time.LocalDateTime;
import java.util.UUID; // Import mới

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository; // Inject mới
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(Role.STUDENT)
                .enabled(true)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = repository.findByUsername(request.getUsername()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        User user = repository.findByUsername(username).orElseThrow();
        if (jwtService.isTokenValid(refreshToken, user)) {
            String accessToken = jwtService.generateToken(user);
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }
        throw new RuntimeException("Invalid refresh token");
    }

    @Transactional
    public void logout(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            User user = repository.findByUsername(username).orElseThrow();
            
            TokenBlacklist blacklist = TokenBlacklist.builder()
                    .tokenString(token)
                    .user(user)
                    .revokedAt(LocalDateTime.now())
                    .build();
            tokenBlacklistRepository.save(blacklist);
            SecurityContextHolder.clearContext();
        }
    }

    @Transactional
    public String createPasswordResetToken(String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Xóa các token cũ của user này để tránh spam
        passwordResetTokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusHours(1)) // Token hết hạn sau 1 giờ
                .build();
        passwordResetTokenRepository.save(resetToken);
        return token;
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken); // Xóa token hết hạn
            throw new RuntimeException("Reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        repository.save(user); // Cập nhật mật khẩu mới

        passwordResetTokenRepository.delete(resetToken); // Vô hiệu hóa token sau khi sử dụng
    }
}
