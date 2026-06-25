package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getTasksForUser(authentication.getName()));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable String status,
                                                       Authentication authentication) {
        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(taskService.getTasksForUserByStatus(authentication.getName(), taskStatus));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task,
                                           Authentication authentication) {
        return ResponseEntity.ok(taskService.createTask(task, authentication.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id,
                                           @Valid @RequestBody Task task,
                                           Authentication authentication) {
        return ResponseEntity.ok(taskService.updateTask(id, task, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id,
                                           Authentication authentication) {
        taskService.deleteTask(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}