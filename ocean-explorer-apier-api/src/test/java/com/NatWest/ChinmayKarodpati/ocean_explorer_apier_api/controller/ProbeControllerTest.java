package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.controller;

import com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.exception.GlobalExceptionHandler;
import com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.service.ProbeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Integration tests for the API Controller.
// We pass mock HTTP requests (simulating real JSON payloads) to make sure 
// everything from serializing data to catching API errors returns correctly.
@WebMvcTest(ProbeController.class)
@Import({ProbeService.class, GlobalExceptionHandler.class})
class ProbeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String MOVE_URL = "/api/probe/move";

    @Nested
    @DisplayName("Successful Requests")
    class SuccessTests {

        @Test
        @DisplayName("Should return 200 with correct response for valid request")
        void moveProbe_validRequest_shouldReturn200WithResult() throws Exception {
            String requestBody = """
                    {
                        "startX": 0,
                        "startY": 0,
                        "direction": "NORTH",
                        "commands": "FFRFF",
                        "gridSize": 5,
                        "obstacles": []
                    }
                    """;

            mockMvc.perform(post(MOVE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.finalX").value(2))
                    .andExpect(jsonPath("$.finalY").value(2))
                    .andExpect(jsonPath("$.direction").value("EAST"))
                    .andExpect(jsonPath("$.visited").isArray())
                    .andExpect(jsonPath("$.visited.length()").value(5));
        }

        @Test
        @DisplayName("Should handle obstacles correctly in response")
        void moveProbe_withObstacles_shouldAvoidThem() throws Exception {
            String requestBody = """
                    {
                        "startX": 0,
                        "startY": 0,
                        "direction": "NORTH",
                        "commands": "FFRFF",
                        "gridSize": 5,
                        "obstacles": [[1, 2], [2, 2]]
                    }
                    """;

            mockMvc.perform(post(MOVE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.finalX").value(0))
                    .andExpect(jsonPath("$.finalY").value(2))
                    .andExpect(jsonPath("$.direction").value("EAST"));
        }

        @Test
        @DisplayName("Should handle empty commands")
        void moveProbe_emptyCommands_shouldReturnStartPosition() throws Exception {
            String requestBody = """
                    {
                        "startX": 2,
                        "startY": 3,
                        "direction": "SOUTH",
                        "commands": "",
                        "gridSize": 5,
                        "obstacles": []
                    }
                    """;

            mockMvc.perform(post(MOVE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.finalX").value(2))
                    .andExpect(jsonPath("$.finalY").value(3))
                    .andExpect(jsonPath("$.direction").value("SOUTH"))
                    .andExpect(jsonPath("$.visited.length()").value(1));
        }

        @Test
        @DisplayName("Should handle request without obstacles field")
        void moveProbe_noObstaclesField_shouldWorkWithoutObstacles() throws Exception {
            String requestBody = """
                    {
                        "startX": 0,
                        "startY": 0,
                        "direction": "EAST",
                        "commands": "FF",
                        "gridSize": 5
                    }
                    """;

            mockMvc.perform(post(MOVE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.finalX").value(2))
                    .andExpect(jsonPath("$.finalY").value(0))
                    .andExpect(jsonPath("$.direction").value("EAST"));
        }
    }

    @Nested
    @DisplayName("Validation Error Responses")
    class ValidationTests {

        @Test
        @DisplayName("Should return 400 for missing required fields")
        void moveProbe_missingFields_shouldReturn400() throws Exception {
            String requestBody = """
                    {
                        "startX": 0
                    }
                    """;

            mockMvc.perform(post(MOVE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Validation failed"))
                    .andExpect(jsonPath("$.details").isArray());
        }

        @Test
        @DisplayName("Should return 400 for invalid direction value")
        void moveProbe_invalidDirection_shouldReturn400() throws Exception {
            String requestBody = """
                    {
                        "startX": 0,
                        "startY": 0,
                        "direction": "UP",
                        "commands": "FF",
                        "gridSize": 5,
                        "obstacles": []
                    }
                    """;

            mockMvc.perform(post(MOVE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for invalid command characters")
        void moveProbe_invalidCommands_shouldReturn400() throws Exception {
            String requestBody = """
                    {
                        "startX": 0,
                        "startY": 0,
                        "direction": "NORTH",
                        "commands": "FXBZ",
                        "gridSize": 5,
                        "obstacles": []
                    }
                    """;

            mockMvc.perform(post(MOVE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for negative startX")
        void moveProbe_negativeStartX_shouldReturn400() throws Exception {
            String requestBody = """
                    {
                        "startX": -1,
                        "startY": 0,
                        "direction": "NORTH",
                        "commands": "F",
                        "gridSize": 5,
                        "obstacles": []
                    }
                    """;

            mockMvc.perform(post(MOVE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for gridSize of 0")
        void moveProbe_zeroGridSize_shouldReturn400() throws Exception {
            String requestBody = """
                    {
                        "startX": 0,
                        "startY": 0,
                        "direction": "NORTH",
                        "commands": "F",
                        "gridSize": 0,
                        "obstacles": []
                    }
                    """;

            mockMvc.perform(post(MOVE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 for empty request body")
        void moveProbe_emptyBody_shouldReturn400() throws Exception {
            mockMvc.perform(post(MOVE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Business Logic Error Responses")
    class BusinessErrorTests {

        @Test
        @DisplayName("Should return 400 when starting position is on obstacle")
        void moveProbe_startOnObstacle_shouldReturn400() throws Exception {
            String requestBody = """
                    {
                        "startX": 1,
                        "startY": 1,
                        "direction": "NORTH",
                        "commands": "F",
                        "gridSize": 5,
                        "obstacles": [[1, 1]]
                    }
                    """;

            mockMvc.perform(post(MOVE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("Should return 400 when starting position is out of grid bounds")
        void moveProbe_startOutOfBounds_shouldReturn400() throws Exception {
            String requestBody = """
                    {
                        "startX": 10,
                        "startY": 10,
                        "direction": "NORTH",
                        "commands": "F",
                        "gridSize": 5,
                        "obstacles": []
                    }
                    """;

            mockMvc.perform(post(MOVE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());
        }
    }
}
