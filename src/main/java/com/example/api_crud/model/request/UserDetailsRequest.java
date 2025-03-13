package com.example.api_crud.model.request;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonFormat;


@Getter
@Setter
public class UserDetailsRequest {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String birthdate;
    private String address;
    private String hobby;
    private String phoneNumber;
}