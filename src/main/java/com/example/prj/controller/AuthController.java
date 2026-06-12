package com.example.prj.controller;

import com.example.prj.model.dto.request.AuthRequest;
import com.example.prj.model.dto.request.ForgotPasswordRequest; // Import mới
import com.example.prj.model.dto.request.RefreshTokenRequest;
import com.example.prj.model.dto.request.RegisterRequest;
import com.example.prj.model.dto.request.ResetPasswordRequest; // Import mới
import com.example.prj.model.dto.response.AuthResponse;
import com.example.prj.model.dto.response.ApiResponse;
import com.example.prj.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse resp = service.register(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Registered successfully", resp));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticate(@RequestBody AuthRequest request) {
        AuthResponse resp = service.authenticate(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Authenticated successfully", resp));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody RefreshTokenRequest request) {
        AuthResponse resp = service.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed successfully", resp));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        service.logout(authHeader);
        return ResponseEntity.ok(new ApiResponse<>(true, "Logged out successfully", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            String resetToken = service.createPasswordResetToken(request.getUsername());
            // Trong môi trường thực tế, token này sẽ được gửi qua email.
            // Ở đây, chúng ta trả về trực tiếp để kiểm thử.
            return ResponseEntity.ok(new ApiResponse<>(true, "Password reset token created. (In real app, this would be emailed)", resetToken));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            service.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse<>(true, "Password has been reset successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
