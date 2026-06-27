package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setStatus(TaskStatus.PENDING);
        task.setUser(user);
    }

    @Test
    void createTask_ShouldSaveAndReturnTask() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task created = taskService.createTask(task, "testuser");

        assertNotNull(created);
        assertEquals("Test Task", created.getTitle());
        assertEquals(TaskStatus.PENDING, created.getStatus());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTask_ShouldUpdateExistingTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task updated = new Task();
        updated.setTitle("Updated");
        updated.setStatus(TaskStatus.COMPLETED);
        when(taskRepository.save(any(Task.class))).thenReturn(updated);

        Task result = taskService.updateTask(1L, updated, "testuser");
        assertEquals("Updated", result.getTitle());
        assertEquals(TaskStatus.COMPLETED, result.getStatus());
    }

    @Test
    void deleteTask_ShouldRemoveTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(1L, "testuser");
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void deleteTask_WhenUserNotAuthorized_ShouldThrow() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        assertThrows(RuntimeException.class, () -> taskService.deleteTask(1L, "wronguser"));
    }
}