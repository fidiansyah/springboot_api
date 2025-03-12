package com.example.api_crud.model.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class UserResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private UUID id;
    private String username;
    private String name;
    private String email;
}
