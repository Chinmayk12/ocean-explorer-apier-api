package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// Test suite for the Grid class.
// We're double-checking that boundary limits, obstacles, and navigation paths are properly validated.
class GridTest {

    @Nested
    @DisplayName("Constructor Validation")
    class ConstructorTests {

        @Test
        @DisplayName("Should throw for zero grid size")
        void constructor_shouldThrowForZeroSize() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Grid(0, null));
        }

        @Test
        @DisplayName("Should throw for negative grid size")
        void constructor_shouldThrowForNegativeSize() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Grid(-5, null));
        }

        @Test
        @DisplayName("Should accept null obstacles (treated as empty)")
        void constructor_shouldAcceptNullObstacles() {
            Grid grid = new Grid(5, null);
            assertNotNull(grid.getObstacles());
            assertTrue(grid.getObstacles().isEmpty());
        }

        @Test
        @DisplayName("Should create grid with valid size")
        void constructor_shouldCreateGridWithValidSize() {
            Grid grid = new Grid(10, new HashSet<>());
            assertEquals(10, grid.getSize());
        }
    }

    @Nested
    @DisplayName("Boundary Tests")
    class BoundaryTests {

        private final Grid grid = new Grid(5, null);

        @ParameterizedTest(name = "({0},{1}) should be within bounds")
        @CsvSource({"0,0", "4,4", "2,3", "0,4", "4,0"})
        void isWithinBounds_shouldReturnTrueForValidPositions(int x, int y) {
            assertTrue(grid.isWithinBounds(new Position(x, y)));
        }

        @ParameterizedTest(name = "({0},{1}) should be OUT of bounds")
        @CsvSource({"-1,0", "0,-1", "5,0", "0,5", "5,5", "-1,-1", "10,10"})
        void isWithinBounds_shouldReturnFalseForOutOfBoundsPositions(int x, int y) {
            assertFalse(grid.isWithinBounds(new Position(x, y)));
        }
    }

    @Nested
    @DisplayName("Obstacle Tests")
    class ObstacleTests {

        @Test
        @DisplayName("Should detect obstacles correctly")
        void isObstacle_shouldReturnTrueForObstaclePositions() {
            Set<Position> obstacles = Set.of(new Position(1, 2), new Position(3, 3));
            Grid grid = new Grid(5, obstacles);

            assertTrue(grid.isObstacle(new Position(1, 2)));
            assertTrue(grid.isObstacle(new Position(3, 3)));
            assertFalse(grid.isObstacle(new Position(0, 0)));
        }

        @Test
        @DisplayName("Obstacle set should be immutable (defensive copy)")
        void getObstacles_shouldReturnImmutableSet() {
            Set<Position> obstacles = new HashSet<>();
            obstacles.add(new Position(1, 1));
            Grid grid = new Grid(5, obstacles);

            // Mutating the original set should not affect the grid
            obstacles.add(new Position(2, 2));
            assertFalse(grid.isObstacle(new Position(2, 2)));

            // Returned set should be immutable
            assertThrows(UnsupportedOperationException.class,
                    () -> grid.getObstacles().add(new Position(3, 3)));
        }
    }

    @Nested
    @DisplayName("Navigability Tests")
    class NavigabilityTests {

        @Test
        @DisplayName("Position within bounds and not an obstacle should be navigable")
        void isNavigable_shouldReturnTrueForFreePositions() {
            Set<Position> obstacles = Set.of(new Position(2, 2));
            Grid grid = new Grid(5, obstacles);

            assertTrue(grid.isNavigable(new Position(0, 0)));
            assertTrue(grid.isNavigable(new Position(4, 4)));
        }

        @Test
        @DisplayName("Position on an obstacle should NOT be navigable")
        void isNavigable_shouldReturnFalseForObstacles() {
            Set<Position> obstacles = Set.of(new Position(2, 2));
            Grid grid = new Grid(5, obstacles);

            assertFalse(grid.isNavigable(new Position(2, 2)));
        }

        @Test
        @DisplayName("Position out of bounds should NOT be navigable")
        void isNavigable_shouldReturnFalseForOutOfBounds() {
            Grid grid = new Grid(5, null);

            assertFalse(grid.isNavigable(new Position(-1, 0)));
            assertFalse(grid.isNavigable(new Position(5, 5)));
        }
    }
}
