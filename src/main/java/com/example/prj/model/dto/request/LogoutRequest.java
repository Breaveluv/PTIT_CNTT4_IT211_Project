package com.example.prj.model.dto.request;

import lombok.Data;

@Data
public class LogoutRequest {
    private String refreshToken;
}
