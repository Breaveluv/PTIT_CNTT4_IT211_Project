package com.example.prj.service;

import com.example.prj.model.dto.request.UserCreateRequest;
import com.example.prj.model.dto.response.UserResponse;
import com.example.prj.model.dto.request.UserUpdateRequest;
import com.example.prj.model.entity.User;
import com.example.prj.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserResponse> getAllUsers(String query, Pageable pageable) {
        Page<User> userPage;
        if (query != null && !query.isEmpty()) {
            userPage = userRepository.searchUsers(query, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return userPage.map(this::mapToUserResponse);
    }

    public UserResponse createUser(UserCreateRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole())
                .enabled(true)
                .build();
        return mapToUserResponse(userRepository.save(user));
    }

    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToUserResponse)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getRole() != null) user.setRole(request.getRole());
        user.setEnabled(request.isActive());
        
        return mapToUserResponse(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.isEnabled())
                .build();
    }
}
