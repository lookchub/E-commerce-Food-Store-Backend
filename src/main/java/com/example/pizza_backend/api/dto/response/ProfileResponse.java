package com.example.pizza_backend.api.dto.response;

import lombok.Data;

@Data
public class ProfileResponse {
    private String profileName;
    private String profileSname;
    private String username;
    private String password;
}
