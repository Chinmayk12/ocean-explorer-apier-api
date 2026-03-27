package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.service;

import com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.dto.ProbeRequest;
import com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.dto.ProbeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Test suite for the ProbeService class.
// We make sure the service properly grabs API inputs, runs them through the models,
// and packages up the responses correctly under different scenarios.
class ProbeServiceTest {

    private ProbeService probeService;

    @BeforeEach
    void setUp() {
        probeService = new ProbeService();
    }

    @Nested
    @DisplayName("Successful Command Processing")
    class SuccessTests {

        @Test
        @DisplayName("Should process FFRFF commands correctly on open grid")
        void processCommands_FFRFF_onOpenGrid_shouldNavigateCorrectly() {
            ProbeRequest request = createRequest(0, 0, "NORTH", "FFRFF", 5, null);

            ProbeResponse response = probeService.processCommands(request);

            assertEquals(2, response.getFinalX());
            assertEquals(2, response.getFinalY());
            assertEquals("EAST", response.getDirection());
            assertEquals(5, response.getVisited().size()); // start + 4 moves
        }

        @Test
        @DisplayName("Should handle FFRFF with obstacles at (1,2) and (2,2)")
        void processCommands_FFRFF_withObstacles_shouldAvoidAndReport() {
            List<int[]> obstacles = Arrays.asList(new int[]{1, 2}, new int[]{2, 2});
            ProbeRequest request = createRequest(0, 0, "NORTH", "FFRFF", 5, obstacles);

            ProbeResponse response = probeService.processCommands(request);

            // Both forward moves after turning east are blocked by obstacles
            assertEquals(0, response.getFinalX());
            assertEquals(2, response.getFinalY());
            assertEquals("EAST", response.getDirection());
        }

        @Test
        @DisplayName("Should handle empty command string")
        void processCommands_emptyCommands_shouldReturnStartPosition() {
            ProbeRequest request = createRequest(2, 3, "SOUTH", "", 5, null);

            ProbeResponse response = probeService.processCommands(request);

            assertEquals(2, response.getFinalX());
            assertEquals(3, response.getFinalY());
            assertEquals("SOUTH", response.getDirection());
            assertEquals(1, response.getVisited().size()); // only start position
        }

        @Test
        @DisplayName("Should handle turns-only commands (no movement)")
        void processCommands_turnsOnly_shouldNotMove() {
            ProbeRequest request = createRequest(1, 1, "NORTH", "LLRR", 5, null);

            ProbeResponse response = probeService.processCommands(request);

            assertEquals(1, response.getFinalX());
            assertEquals(1, response.getFinalY());
            assertEquals("NORTH", response.getDirection());
            assertEquals(1, response.getVisited().size());
        }

        @Test
        @DisplayName("Should handle lowercase commands")
        void processCommands_lowercaseCommands_shouldWork() {
            ProbeRequest request = createRequest(0, 0, "NORTH", "ff", 5, null);

            ProbeResponse response = probeService.processCommands(request);

            assertEquals(0, response.getFinalX());
            assertEquals(2, response.getFinalY());
        }

        @Test
        @DisplayName("Should track all visited positions correctly")
        void processCommands_shouldTrackVisitedPositions() {
            ProbeRequest request = createRequest(0, 0, "NORTH", "FF", 5, null);

            ProbeResponse response = probeService.processCommands(request);

            List<int[]> visited = response.getVisited();
            assertEquals(3, visited.size());
            assertArrayEquals(new int[]{0, 0}, visited.get(0));
            assertArrayEquals(new int[]{0, 1}, visited.get(1));
            assertArrayEquals(new int[]{0, 2}, visited.get(2));
        }

        @Test
        @DisplayName("Should handle null obstacles list")
        void processCommands_nullObstacles_shouldWorkWithoutObstacles() {
            ProbeRequest request = createRequest(0, 0, "EAST", "FFF", 5, null);

            ProbeResponse response = probeService.processCommands(request);

            assertEquals(3, response.getFinalX());
            assertEquals(0, response.getFinalY());
        }

        @Test
        @DisplayName("Should handle empty obstacles list")
        void processCommands_emptyObstacles_shouldWorkWithoutObstacles() {
            ProbeRequest request = createRequest(0, 0, "EAST", "FFF", 5,
                    Collections.emptyList());

            ProbeResponse response = probeService.processCommands(request);

            assertEquals(3, response.getFinalX());
            assertEquals(0, response.getFinalY());
        }

        @Test
        @DisplayName("Backward movement through service")
        void processCommands_backwardMovement_shouldWork() {
            ProbeRequest request = createRequest(2, 2, "NORTH", "BB", 5, null);

            ProbeResponse response = probeService.processCommands(request);

            assertEquals(2, response.getFinalX());
            assertEquals(0, response.getFinalY());
            assertEquals("NORTH", response.getDirection());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class ErrorTests {

        @Test
        @DisplayName("Should throw for invalid direction string")
        void processCommands_invalidDirection_shouldThrow() {
            ProbeRequest request = createRequest(0, 0, "UP", "FF", 5, null);

            assertThrows(IllegalArgumentException.class,
                    () -> probeService.processCommands(request));
        }

        @Test
        @DisplayName("Should throw for starting position on obstacle")
        void processCommands_startOnObstacle_shouldThrow() {
            List<int[]> obstacles = List.of(new int[]{0, 0});
            ProbeRequest request = createRequest(0, 0, "NORTH", "F", 5, obstacles);

            assertThrows(IllegalArgumentException.class,
                    () -> probeService.processCommands(request));
        }

        @Test
        @DisplayName("Should throw for starting position out of bounds")
        void processCommands_startOutOfBounds_shouldThrow() {
            ProbeRequest request = createRequest(10, 10, "NORTH", "F", 5, null);

            assertThrows(IllegalArgumentException.class,
                    () -> probeService.processCommands(request));
        }

        @Test
        @DisplayName("Should throw for malformed obstacle array")
        void processCommands_malformedObstacle_shouldThrow() {
            List<int[]> obstacles = List.of(new int[]{1}); // Only one coordinate
            ProbeRequest request = createRequest(0, 0, "NORTH", "F", 5, obstacles);

            assertThrows(IllegalArgumentException.class,
                    () -> probeService.processCommands(request));
        }

        @Test
        @DisplayName("Should throw for invalid command character")
        void processCommands_invalidCommandChar_shouldThrow() {
            ProbeRequest request = createRequest(0, 0, "NORTH", "FXF", 5, null);

            assertThrows(IllegalArgumentException.class,
                    () -> probeService.processCommands(request));
        }
    }

    @Nested
    @DisplayName("Boundary Behavior through Service")
    class BoundaryTests {

        @Test
        @DisplayName("Should stop at north boundary")
        void processCommands_hitNorthBoundary_shouldStopAtEdge() {
            // Grid is 3x3, start at (0,0) facing NORTH, move FFFFF
            ProbeRequest request = createRequest(0, 0, "NORTH", "FFFFF", 3, null);

            ProbeResponse response = probeService.processCommands(request);

            assertEquals(0, response.getFinalX());
            assertEquals(2, response.getFinalY()); // max y = 2 for grid size 3
        }

        @Test
        @DisplayName("Should stop at south boundary")
        void processCommands_hitSouthBoundary_shouldStopAtEdge() {
            ProbeRequest request = createRequest(0, 2, "SOUTH", "FFFFF", 3, null);

            ProbeResponse response = probeService.processCommands(request);

            assertEquals(0, response.getFinalX());
            assertEquals(0, response.getFinalY());
        }

        @Test
        @DisplayName("Grid size of 1 should leave probe in place for any moves")
        void processCommands_gridSizeOne_shouldNotMove() {
            ProbeRequest request = createRequest(0, 0, "NORTH", "FFRFF", 1, null);

            ProbeResponse response = probeService.processCommands(request);

            assertEquals(0, response.getFinalX());
            assertEquals(0, response.getFinalY());
        }
    }

    // --- Helper Method ---

    /**
     * Creates a ProbeRequest with the given parameters.
     */
    private ProbeRequest createRequest(int startX, int startY, String direction,
                                       String commands, int gridSize,
                                       List<int[]> obstacles) {
        ProbeRequest request = new ProbeRequest();
        request.setStartX(startX);
        request.setStartY(startY);
        request.setDirection(direction);
        request.setCommands(commands);
        request.setGridSize(gridSize);
        request.setObstacles(obstacles);
        return request;
    }
}
