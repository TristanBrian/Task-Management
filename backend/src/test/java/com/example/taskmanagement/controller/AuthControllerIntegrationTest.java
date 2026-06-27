package com.example.taskmanagement.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")   // uses H2 in-memory DB
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String uniqueSuffix;

    @BeforeEach
    void setUp() {
        uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    void registerUser_ShouldReturnOk() throws Exception {
        String userJson = String.format(
                "{\"username\":\"integration_%s\",\"email\":\"int_%s@test.com\",\"password\":\"secret123\"}",
                uniqueSuffix, uniqueSuffix);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void login_ShouldReturnToken() throws Exception {
        // Register a unique user first
        String userJson = String.format(
                "{\"username\":\"login_%s\",\"email\":\"login_%s@test.com\",\"password\":\"secret123\"}",
                uniqueSuffix, uniqueSuffix);
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk());

        // Now login with the same credentials
        String loginJson = String.format(
                "{\"username\":\"login_%s\",\"password\":\"secret123\"}",
                uniqueSuffix);
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        String loginJson = "{\"username\":\"nonexistent\",\"password\":\"wrong\"}";
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isUnauthorized());
    }
}