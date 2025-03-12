package com.example.api_crud.controller;

import java.util.LinkedHashMap;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.api_crud.model.request.UpdateUserRequest;
import com.example.api_crud.model.request.UserRequest;
import com.example.api_crud.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping("/api/users")
@Tag(name = "User Management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<LinkedHashMap<String, Object>> getAllUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        return userService.getAllUsers(page, size);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<LinkedHashMap<String, Object>> getUserByUuid(@PathVariable UUID uuid) {
        return userService.getUserByUuid(uuid);
    }

    @PostMapping
    public ResponseEntity<LinkedHashMap<String, Object>> createUser(@RequestBody UserRequest userRequest) {
        return userService.createUser(userRequest);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<LinkedHashMap<String, Object>> updateUser(
            @PathVariable UUID uuid,
            @RequestBody UpdateUserRequest updateRequest) {
        return userService.updateUser(uuid, updateRequest);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<LinkedHashMap<String, Object>> deleteUser(@PathVariable UUID uuid) {
        return userService.deleteUser(uuid);
    }
}