package com.example.prj.controller;

import com.example.prj.model.dto.response.UserResponse;
import com.example.prj.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class rAdminUserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminUserController adminUserController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_Success() {
        PageRequest pageable = PageRequest.of(0, 10);
        UserResponse response = UserResponse.builder().username("test").build();
        Page<UserResponse> page = new PageImpl<>(List.of(response));

        when(userService.getAllUsers(null, pageable)).thenReturn(page);

        var result = adminUserController.getAllUsers(null, pageable);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(1, result.getBody().getData().getContent().size());
    }

    @Test
    void getUserById_Success() {
        UserResponse response = UserResponse.builder().id(1L).username("test").build();
        when(userService.getUserById(1L)).thenReturn(response);

        var result = adminUserController.getUserById(1L);

        assertEquals(200, result.getStatusCode().value());
        assertEquals("test", result.getBody().getData().getUsername());
    }

    @Test
    void deleteUser_Success() {
        doNothing().when(userService).deleteUser(1L);
        var result = adminUserController.deleteUser(1L);
        assertEquals(204, result.getStatusCode().value());
    }

    @Test
    void searchUsers_Success() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(userService.getAllUsers("test", pageable)).thenReturn(Page.empty());

        var result = adminUserController.getAllUsers("test", pageable);

        assertEquals(200, result.getStatusCode().value());
        verify(userService, times(1)).getAllUsers("test", pageable);
    }

    @Test
    void updateUser_Success() {
        UserResponse response = UserResponse.builder().id(1L).fullName("New Name").build();
        when(userService.updateUser(eq(1L), any())).thenReturn(response);

        var result = adminUserController.updateUser(1L, any());

        assertEquals(200, result.getStatusCode().value());
        assertEquals("New Name", result.getBody().getData().getFullName());
    }
}
