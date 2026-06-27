package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.UserRequest;
import com.example.taskmanagement.dto.UserResponse;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String token = authService.authenticate(credentials.get("username"), credentials.get("password"));
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRequest userRequest) {
        // Convert DTO to entity
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword()); // will be encoded in service

        User registered = authService.register(user);

        // Convert entity to response DTO
        UserResponse response = new UserResponse();
        response.setId(registered.getId());
        response.setUsername(registered.getUsername());
        response.setEmail(registered.getEmail());

        return ResponseEntity.ok(Map.of("id", response.getId(), "message", "User registered"));
    }
}