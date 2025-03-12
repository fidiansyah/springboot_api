package com.example.api_crud.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateUserRequest {
    private String username;
    private String name;    
    private String email;
    private String password;
}

