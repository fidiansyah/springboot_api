package com.example.api_crud.controller;

import com.example.api_crud.model.request.UserDetailsRequest;
import com.example.api_crud.service.UserDetailsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.LinkedHashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{uuid}/details")
@Tag(name = "User Details Management")
public class UserDetailsController {

    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public UserDetailsController(UserDetailsService userDetailsService, ObjectMapper objectMapper) {
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<LinkedHashMap<String, Object>> getUserDetails(
            @PathVariable UUID uuid) {
        return userDetailsService.getUserDetails(uuid);
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LinkedHashMap<String, Object>> updateUserDetails(
            @PathVariable UUID uuid,
            @RequestPart(value = "request", required = false) String requestJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {

        UserDetailsRequest request = null; // Initialize with null
        LinkedHashMap<String, Object> errorResponse = new LinkedHashMap<>();

        try {
            if (requestJson != null) {
                request = objectMapper.readValue(requestJson, UserDetailsRequest.class);
            } else {
                request = new UserDetailsRequest(); // Create empty request if not provided
            }
        } catch (JsonProcessingException e) {
            errorResponse.put("status", "error");
            errorResponse.put("message", "Invalid JSON format");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        return userDetailsService.updateUserDetails(uuid, request, photo);
    }
}