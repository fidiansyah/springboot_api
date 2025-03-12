package com.example.api_crud.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequest {
    @NotBlank(message = "Username tidak boleh kosong")
    private String username;
    
    @NotBlank(message = "Nama tidak boleh kosong")
    private String name;    
    
    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    private String email;
    
    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 8, message = "Password harus minimal 8 karakter")
    private String password;
}
