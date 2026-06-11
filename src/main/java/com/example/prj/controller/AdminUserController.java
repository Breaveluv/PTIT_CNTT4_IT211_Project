package com.example.prj.controller;

import com.example.prj.model.dto.request.UserCreateRequest;
import com.example.prj.model.dto.response.UserResponse;
import com.example.prj.model.dto.request.UserUpdateRequest;
import com.example.prj.service.UserService;
import com.example.prj.model.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(required = false) String query,
            Pageable pageable) {
        Page<UserResponse> page = userService.getAllUsers(query, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched users", page));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse resp = userService.createUser(request);
        return new ResponseEntity<>(new ApiResponse<>(true, "Created user successfully", resp), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse resp = userService.getUserById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Fetched user", resp));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        UserResponse resp = userService.updateUser(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Updated user successfully", resp));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
