package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @BeforeEach
    void cleanAndSetup() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        // Create a test user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@test.com");
        authService.register(user);
    }

    @Test
    @WithMockUser(username = "testuser")
    void createTask_ShouldReturnCreatedTask() throws Exception {
        Task task = new Task();
        task.setTitle("Integration Task");
        task.setStatus(TaskStatus.PENDING);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Task"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTasks_ShouldReturnPage() throws Exception {
        // Create 3 tasks
        for (int i = 0; i < 3; i++) {
            Task task = new Task();
            task.setTitle("Task " + i);
            task.setStatus(TaskStatus.PENDING);
            mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(task)))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/tasks?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").isNumber())
                .andExpect(jsonPath("$.pageable").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTasks_WithStatusFilter_ShouldReturnFilteredPage() throws Exception {
        Task completed = new Task();
        completed.setTitle("Completed Task");
        completed.setStatus(TaskStatus.COMPLETED);
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completed)))
                .andExpect(status().isOk());

        Task pending = new Task();
        pending.setTitle("Pending Task");
        pending.setStatus(TaskStatus.PENDING);
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pending)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tasks?status=COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].status").value("COMPLETED"));
    }
}