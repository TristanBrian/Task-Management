package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<Task> getTasksForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return taskRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Task> getTasksForUserByStatus(String username, TaskStatus status) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return taskRepository.findByUserAndStatus(user, status);
    }

    @Transactional
    public Task createTask(Task task, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        task.setUser(user);
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long id, Task updatedTask, String username) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!existing.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized");
        }
        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setStatus(updatedTask.getStatus());
        return taskRepository.save(existing);
    }

    @Transactional
    public void deleteTask(Long id, String username) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized");
        }
        taskRepository.delete(task);
    }
}