package com.example.api_crud.model.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class UserDetailsResponse {
    private LocalDate birthdate;
    private String address;
    private String hobby;
    private String phoneNumber;
    private String photoUrl;
}