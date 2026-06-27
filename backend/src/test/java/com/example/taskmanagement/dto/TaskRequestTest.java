package com.example.taskmanagement.dto;

import com.example.taskmanagement.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TaskRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validTaskRequest_ShouldPassValidation() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Description");
        request.setStatus(TaskStatus.PENDING);

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void titleBlank_ShouldFailValidation() {
        TaskRequest request = new TaskRequest();
        request.setTitle("");
        request.setStatus(TaskStatus.PENDING);

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Title is required", violations.iterator().next().getMessage());
    }

    @Test
    void titleTooLong_ShouldFailValidation() {
        TaskRequest request = new TaskRequest();
        request.setTitle("a".repeat(101));
        request.setStatus(TaskStatus.PENDING);

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Title must not exceed 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    void statusNull_ShouldFailValidation() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Test");
        request.setStatus(null);

        Set<ConstraintViolation<TaskRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertEquals("Status is required", violations.iterator().next().getMessage());
    }
}