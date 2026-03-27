package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

// Test suite for the Direction enum.
// Checks if turning logic (like changing from North to West) and coordinate movements work for all map directions.
class DirectionTest {

    @Nested
    @DisplayName("Turn Left Tests")
    class TurnLeftTests {

        @ParameterizedTest(name = "{0} → turnLeft → {1}")
        @CsvSource({
                "NORTH, WEST",
                "WEST,  SOUTH",
                "SOUTH, EAST",
                "EAST,  NORTH"
        })
        void turnLeft_shouldRotateCounterClockwise(Direction initial, Direction expected) {
            assertEquals(expected, initial.turnLeft());
        }

        @Test
        @DisplayName("Four left turns should return to original direction")
        void fourLeftTurns_shouldReturnToOriginal() {
            Direction direction = Direction.NORTH;
            for (int i = 0; i < 4; i++) {
                direction = direction.turnLeft();
            }
            assertEquals(Direction.NORTH, direction);
        }
    }

    @Nested
    @DisplayName("Turn Right Tests")
    class TurnRightTests {

        @ParameterizedTest(name = "{0} → turnRight → {1}")
        @CsvSource({
                "NORTH, EAST",
                "EAST,  SOUTH",
                "SOUTH, WEST",
                "WEST,  NORTH"
        })
        void turnRight_shouldRotateClockwise(Direction initial, Direction expected) {
            assertEquals(expected, initial.turnRight());
        }

        @Test
        @DisplayName("Four right turns should return to original direction")
        void fourRightTurns_shouldReturnToOriginal() {
            Direction direction = Direction.NORTH;
            for (int i = 0; i < 4; i++) {
                direction = direction.turnRight();
            }
            assertEquals(Direction.NORTH, direction);
        }
    }

    @Nested
    @DisplayName("Movement Delta Tests")
    class MovementDeltaTests {

        @ParameterizedTest(name = "{0} → dx={1}, dy={2}")
        @CsvSource({
                "NORTH, 0,  1",
                "SOUTH, 0, -1",
                "EAST,  1,  0",
                "WEST, -1,  0"
        })
        void direction_shouldHaveCorrectMovementDeltas(Direction direction, int dx, int dy) {
            assertEquals(dx, direction.getDx());
            assertEquals(dy, direction.getDy());
        }
    }

    @Test
    @DisplayName("Left turn followed by right turn should cancel out")
    void leftThenRight_shouldCancelOut() {
        for (Direction dir : Direction.values()) {
            assertEquals(dir, dir.turnLeft().turnRight(),
                    "Left then right should return to " + dir);
        }
    }
}
