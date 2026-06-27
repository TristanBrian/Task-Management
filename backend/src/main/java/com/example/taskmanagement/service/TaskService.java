package com.example.taskmanagement.service;

import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.exception.UnauthorizedException;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Paginated versions
    public Page<Task> getTasksForUser(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return taskRepository.findByUser(user, pageable);
    }

    public Page<Task> getTasksForUserByStatus(String username, TaskStatus status, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return taskRepository.findByUserAndStatus(user, status, pageable);
    }

    // CRUD methods remain the same, but replace RuntimeException with custom exceptions
    @Transactional
    public Task createTask(Task task, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        task.setUser(user);
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long id, Task updatedTask, String username) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!existing.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not authorized to update this task");
        }
        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setStatus(updatedTask.getStatus());
        return taskRepository.save(existing);
    }

    @Transactional
    public void deleteTask(Long id, String username) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!task.getUser().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not authorized to delete this task");
        }
        taskRepository.delete(task);
    }
}