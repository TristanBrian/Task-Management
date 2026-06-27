package com.example.taskmanagement.dto;

import com.example.taskmanagement.model.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskRequestSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void serializeAndDeserialize_ShouldWork() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Test");
        request.setDescription("Desc");
        request.setStatus(TaskStatus.PENDING);

        String json = mapper.writeValueAsString(request);
        TaskRequest deserialized = mapper.readValue(json, TaskRequest.class);

        assertEquals(request.getTitle(), deserialized.getTitle());
        assertEquals(request.getDescription(), deserialized.getDescription());
        assertEquals(request.getStatus(), deserialized.getStatus());
    }
}