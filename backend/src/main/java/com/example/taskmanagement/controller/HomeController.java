package com.example.taskmanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Task Management API is running. "
                + "Visit <a href=\"/swagger-ui.html\">Swagger UI</a> for documentation.";
    }
}