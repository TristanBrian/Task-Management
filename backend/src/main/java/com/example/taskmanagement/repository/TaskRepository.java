package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserOrderByCreatedAtDesc(User user);
    List<Task> findByUserAndStatus(User user, TaskStatus status);
}