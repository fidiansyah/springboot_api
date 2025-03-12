package com.example.api_crud.model.response;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class UserResponse {
    private UUID id;
    private String username;
    private String name;
    private String email;
}
