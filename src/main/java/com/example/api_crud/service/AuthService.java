package com.example.api_crud.service;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.api_crud.config.JwtUtil;
import com.example.api_crud.entity.UserEntity;
import com.example.api_crud.model.request.LoginRequest;
import com.example.api_crud.model.request.UserRequest;
import com.example.api_crud.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<LinkedHashMap<String, Object>> registerUser(UserRequest userReq) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();

        try {
            Optional<UserEntity> existingUser = userRepository.findByUsername(userReq.getUsername());
            if (existingUser.isPresent()) {
                response.put("status", "error");
                response.put("message", "Username sudah digunakan!");
                return ResponseEntity.badRequest().body(response);
            }

            String hashedPassword = passwordEncoder.encode(userReq.getPassword());

            UserEntity newUser = new UserEntity();
            newUser.setUuid(UUID.randomUUID());
            newUser.setUsername(userReq.getUsername());
            newUser.setName(userReq.getName());
            newUser.setEmail(userReq.getEmail());
            newUser.setPassword(hashedPassword);

            userRepository.save(newUser);

            response.put("status", "success");
            response.put("message", "User berhasil didaftarkan!");
            response.put("user", newUser.getUsername());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Terjadi kesalahan saat registrasi: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    public ResponseEntity<LinkedHashMap<String, Object>> loginUser(LoginRequest loginRequest) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        
        try {
            Optional<UserEntity> userOpt = userRepository.findByUsername(loginRequest.getUsername());
            
            if (userOpt.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Username tidak ditemukan");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            UserEntity user = userOpt.get();
            
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                response.put("status", "error");
                response.put("message", "Password salah");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Generate JWT
            String token = jwtUtil.createToken(user.getUuid().toString());
            
            response.put("status", "success");
            response.put("message", "Login berhasil");
            response.put("token", token);
            response.put("user", user.getUsername());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
}
