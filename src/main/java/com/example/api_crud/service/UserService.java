package com.example.api_crud.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.api_crud.entity.UserEntity;
import com.example.api_crud.model.request.UpdateUserRequest;
import com.example.api_crud.model.request.UserRequest;
import com.example.api_crud.model.response.UserResponse;
import com.example.api_crud.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

public ResponseEntity<LinkedHashMap<String, Object>> getAllUsers(int page, int size) {
    LinkedHashMap<String, Object> response = new LinkedHashMap<>();
    try {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> userPage = userRepository.findAll(pageable);
        
        List<UserResponse> userResponses = userPage.getContent()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        // Pagination metadata
        LinkedHashMap<String, Object> pagination = new LinkedHashMap<>();
        pagination.put("currentPage", userPage.getNumber());
        pagination.put("totalItems", userPage.getTotalElements());
        pagination.put("totalPages", userPage.getTotalPages());
        pagination.put("pageSize", userPage.getSize());
        
        response.put("status", "success");
        response.put("message", "Users retrieved successfully");
        response.put("data", userResponses);
        response.put("pagination", pagination);
        
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        response.put("status", "error");
        response.put("message", "Error retrieving users: " + e.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }
}    public ResponseEntity<LinkedHashMap<String, Object>> getUserByUuid(UUID uuid) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            Optional<UserEntity> userOpt = userRepository.findByUuid(uuid);
            if (userOpt.isEmpty()) {
                response.put("status", "error");
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("status", "success");
            response.put("message", "User retrieved successfully");
            response.put("data", convertToResponse(userOpt.get()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public ResponseEntity<LinkedHashMap<String, Object>> createUser(UserRequest userRequest) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
                response.put("status", "error");
                response.put("message", "Username already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }

            UserEntity newUser = new UserEntity();
            newUser.setUuid(UUID.randomUUID());
            newUser.setUsername(userRequest.getUsername());
            newUser.setName(userRequest.getName());
            newUser.setEmail(userRequest.getEmail());
            newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            
            userRepository.save(newUser);
            
            response.put("status", "success");
            response.put("message", "User created successfully");
            response.put("data", convertToResponse(newUser));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error creating user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public ResponseEntity<LinkedHashMap<String, Object>> updateUser(UUID uuid, UpdateUserRequest updateRequest) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            Optional<UserEntity> userOpt = userRepository.findByUuid(uuid);
            if (userOpt.isEmpty()) {
                response.put("status", "error");
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            UserEntity user = userOpt.get();
            
            // Update username
            if (updateRequest.getUsername() != null && !updateRequest.getUsername().isEmpty()) {
                if (userRepository.findByUsername(updateRequest.getUsername())
                        .filter(u -> !u.getUuid().equals(uuid))
                        .isPresent()) {
                    response.put("status", "error");
                    response.put("message", "Username already taken");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                user.setUsername(updateRequest.getUsername());
            }
            
            // Update other fields
            if (updateRequest.getName() != null) user.setName(updateRequest.getName());
            if (updateRequest.getEmail() != null) user.setEmail(updateRequest.getEmail());
            if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            }
            
            userRepository.save(user);
            
            response.put("status", "success");
            response.put("message", "User updated successfully");
            response.put("data", convertToResponse(user));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error updating user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public ResponseEntity<LinkedHashMap<String, Object>> deleteUser(UUID uuid) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        try {
            Optional<UserEntity> userOpt = userRepository.findByUuid(uuid);
            if (userOpt.isEmpty()) {
                response.put("status", "error");
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            userRepository.delete(userOpt.get());
            
            response.put("status", "success");
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error deleting user: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    private UserResponse convertToResponse(UserEntity user) {
        UserResponse response = new UserResponse();
        response.setId(user.getUuid());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        return response;
    }
}