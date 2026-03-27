package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// Test suite for the Probe class.
// Here we make sure the probe moves, turns, avoids obstacles, and tracks where it's been correctly.
class ProbeTest {

    private Grid grid;

    @BeforeEach
    void setUp() {
        // Default 5x5 grid with no obstacles
        grid = new Grid(5, null);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create probe at valid starting position")
        void constructor_shouldCreateProbeAtValidPosition() {
            Probe probe = new Probe(new Position(0, 0), Direction.NORTH, grid);

            assertEquals(new Position(0, 0), probe.getPosition());
            assertEquals(Direction.NORTH, probe.getDirection());
        }

        @Test
        @DisplayName("Visited positions should contain starting position")
        void constructor_shouldRecordStartingPosition() {
            Probe probe = new Probe(new Position(2, 3), Direction.EAST, grid);

            assertEquals(1, probe.getVisitedPositions().size());
            assertEquals(new Position(2, 3), probe.getVisitedPositions().get(0));
        }

        @Test
        @DisplayName("Should throw for starting position on an obstacle")
        void constructor_shouldThrowForObstacleStartPosition() {
            Grid gridWithObstacles = new Grid(5, Set.of(new Position(1, 1)));

            assertThrows(IllegalArgumentException.class,
                    () -> new Probe(new Position(1, 1), Direction.NORTH, gridWithObstacles));
        }

        @Test
        @DisplayName("Should throw for starting position out of bounds")
        void constructor_shouldThrowForOutOfBoundsStartPosition() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Probe(new Position(5, 5), Direction.NORTH, grid));
        }
    }

    @Nested
    @DisplayName("Forward Movement Tests")
    class ForwardMovementTests {

        @Test
        @DisplayName("Should move north (y+1)")
        void moveForward_facingNorth_shouldIncreaseY() {
            Probe probe = new Probe(new Position(2, 2), Direction.NORTH, grid);
            probe.execute(Command.F);

            assertEquals(new Position(2, 3), probe.getPosition());
        }

        @Test
        @DisplayName("Should move east (x+1)")
        void moveForward_facingEast_shouldIncreaseX() {
            Probe probe = new Probe(new Position(2, 2), Direction.EAST, grid);
            probe.execute(Command.F);

            assertEquals(new Position(3, 2), probe.getPosition());
        }

        @Test
        @DisplayName("Should move south (y-1)")
        void moveForward_facingSouth_shouldDecreaseY() {
            Probe probe = new Probe(new Position(2, 2), Direction.SOUTH, grid);
            probe.execute(Command.F);

            assertEquals(new Position(2, 1), probe.getPosition());
        }

        @Test
        @DisplayName("Should move west (x-1)")
        void moveForward_facingWest_shouldDecreaseX() {
            Probe probe = new Probe(new Position(2, 2), Direction.WEST, grid);
            probe.execute(Command.F);

            assertEquals(new Position(1, 2), probe.getPosition());
        }
    }

    @Nested
    @DisplayName("Backward Movement Tests")
    class BackwardMovementTests {

        @Test
        @DisplayName("Backward while facing north should decrease y")
        void moveBackward_facingNorth_shouldDecreaseY() {
            Probe probe = new Probe(new Position(2, 2), Direction.NORTH, grid);
            probe.execute(Command.B);

            assertEquals(new Position(2, 1), probe.getPosition());
        }

        @Test
        @DisplayName("Backward while facing east should decrease x")
        void moveBackward_facingEast_shouldDecreaseX() {
            Probe probe = new Probe(new Position(2, 2), Direction.EAST, grid);
            probe.execute(Command.B);

            assertEquals(new Position(1, 2), probe.getPosition());
        }

        @Test
        @DisplayName("Backward should not change direction")
        void moveBackward_shouldNotChangeDirection() {
            Probe probe = new Probe(new Position(2, 2), Direction.NORTH, grid);
            probe.execute(Command.B);

            assertEquals(Direction.NORTH, probe.getDirection());
        }
    }

    @Nested
    @DisplayName("Turning Tests")
    class TurningTests {

        @Test
        @DisplayName("Turn left from NORTH should face WEST")
        void turnLeft_fromNorth_shouldFaceWest() {
            Probe probe = new Probe(new Position(2, 2), Direction.NORTH, grid);
            probe.execute(Command.L);

            assertEquals(Direction.WEST, probe.getDirection());
            // Position should not change on turn
            assertEquals(new Position(2, 2), probe.getPosition());
        }

        @Test
        @DisplayName("Turn right from NORTH should face EAST")
        void turnRight_fromNorth_shouldFaceEast() {
            Probe probe = new Probe(new Position(2, 2), Direction.NORTH, grid);
            probe.execute(Command.R);

            assertEquals(Direction.EAST, probe.getDirection());
        }

        @Test
        @DisplayName("Turning should not add to visited positions")
        void turn_shouldNotAddToVisitedPositions() {
            Probe probe = new Probe(new Position(2, 2), Direction.NORTH, grid);
            probe.execute(Command.L);
            probe.execute(Command.R);

            // Only the starting position should be recorded
            assertEquals(1, probe.getVisitedPositions().size());
        }
    }

    @Nested
    @DisplayName("Boundary Enforcement Tests")
    class BoundaryTests {

        @Test
        @DisplayName("Should not move past north boundary")
        void moveForward_atNorthBoundary_shouldStayInPlace() {
            Probe probe = new Probe(new Position(2, 4), Direction.NORTH, grid);
            probe.execute(Command.F);

            assertEquals(new Position(2, 4), probe.getPosition(),
                    "Probe should stay at boundary");
        }

        @Test
        @DisplayName("Should not move past south boundary")
        void moveForward_atSouthBoundary_shouldStayInPlace() {
            Probe probe = new Probe(new Position(2, 0), Direction.SOUTH, grid);
            probe.execute(Command.F);

            assertEquals(new Position(2, 0), probe.getPosition());
        }

        @Test
        @DisplayName("Should not move past east boundary")
        void moveForward_atEastBoundary_shouldStayInPlace() {
            Probe probe = new Probe(new Position(4, 2), Direction.EAST, grid);
            probe.execute(Command.F);

            assertEquals(new Position(4, 2), probe.getPosition());
        }

        @Test
        @DisplayName("Should not move past west boundary")
        void moveForward_atWestBoundary_shouldStayInPlace() {
            Probe probe = new Probe(new Position(0, 2), Direction.WEST, grid);
            probe.execute(Command.F);

            assertEquals(new Position(0, 2), probe.getPosition());
        }

        @Test
        @DisplayName("Backward should also respect boundaries")
        void moveBackward_atBoundary_shouldStayInPlace() {
            Probe probe = new Probe(new Position(0, 0), Direction.NORTH, grid);
            probe.execute(Command.B); // Would go to (0, -1) — out of bounds

            assertEquals(new Position(0, 0), probe.getPosition());
        }

        @Test
        @DisplayName("Blocked move should not add to visited positions")
        void blockedMove_shouldNotAddToVisitedPositions() {
            Probe probe = new Probe(new Position(2, 4), Direction.NORTH, grid);
            probe.execute(Command.F); // Blocked

            assertEquals(1, probe.getVisitedPositions().size(),
                    "Blocked move should not add to visited list");
        }
    }

    @Nested
    @DisplayName("Obstacle Avoidance Tests")
    class ObstacleTests {

        @Test
        @DisplayName("Should not move onto an obstacle")
        void moveForward_intoObstacle_shouldStayInPlace() {
            Grid gridWithObstacles = new Grid(5, Set.of(new Position(2, 3)));
            Probe probe = new Probe(new Position(2, 2), Direction.NORTH, gridWithObstacles);

            probe.execute(Command.F);

            assertEquals(new Position(2, 2), probe.getPosition(),
                    "Probe should not move onto obstacle");
        }

        @Test
        @DisplayName("Should skip obstacle and continue executing other commands")
        void multipleCommands_withObstacle_shouldSkipBlockedAndContinue() {
            Grid gridWithObstacles = new Grid(5, Set.of(new Position(2, 3)));
            Probe probe = new Probe(new Position(2, 2), Direction.NORTH, gridWithObstacles);

            // F → blocked (obstacle at 2,3), R → face EAST, F → move to (3,2)
            probe.execute(Command.F);
            probe.execute(Command.R);
            probe.execute(Command.F);

            assertEquals(new Position(3, 2), probe.getPosition());
            assertEquals(Direction.EAST, probe.getDirection());
        }

        @Test
        @DisplayName("Backward into obstacle should be blocked")
        void moveBackward_intoObstacle_shouldStayInPlace() {
            Grid gridWithObstacles = new Grid(5, Set.of(new Position(2, 1)));
            Probe probe = new Probe(new Position(2, 2), Direction.NORTH, gridWithObstacles);

            probe.execute(Command.B); // Would go to (2,1) — obstacle

            assertEquals(new Position(2, 2), probe.getPosition());
        }
    }

    @Nested
    @DisplayName("Visited Position Tracking Tests")
    class VisitedTrackingTests {

        @Test
        @DisplayName("Should track all visited positions in order")
        void visitedPositions_shouldTrackInOrder() {
            Probe probe = new Probe(new Position(0, 0), Direction.NORTH, grid);
            probe.execute(Command.F); // (0,1)
            probe.execute(Command.F); // (0,2)
            probe.execute(Command.R); // face EAST
            probe.execute(Command.F); // (1,2)

            List<Position> visited = probe.getVisitedPositions();
            assertEquals(4, visited.size());
            assertEquals(new Position(0, 0), visited.get(0));
            assertEquals(new Position(0, 1), visited.get(1));
            assertEquals(new Position(0, 2), visited.get(2));
            assertEquals(new Position(1, 2), visited.get(3));
        }

        @Test
        @DisplayName("Visited list should be unmodifiable")
        void getVisitedPositions_shouldReturnUnmodifiableList() {
            Probe probe = new Probe(new Position(0, 0), Direction.NORTH, grid);

            assertThrows(UnsupportedOperationException.class,
                    () -> probe.getVisitedPositions().add(new Position(9, 9)));
        }

        @Test
        @DisplayName("Revisiting a position should add it again to visited list")
        void revisitedPosition_shouldBeRecordedAgain() {
            Probe probe = new Probe(new Position(2, 2), Direction.NORTH, grid);
            probe.execute(Command.F); // (2,3)
            probe.execute(Command.B); // (2,2) — revisited

            List<Position> visited = probe.getVisitedPositions();
            assertEquals(3, visited.size());
            assertEquals(new Position(2, 2), visited.get(0));
            assertEquals(new Position(2, 3), visited.get(1));
            assertEquals(new Position(2, 2), visited.get(2));
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationTests {

        @Test
        @DisplayName("Example from requirements: FFRFF on 5x5 grid with obstacles")
        void requirementExample_shouldProduceExpectedResult() {
            Set<Position> obstacles = Set.of(new Position(1, 2), new Position(2, 2));
            Grid gridWithObs = new Grid(5, obstacles);
            Probe probe = new Probe(new Position(0, 0), Direction.NORTH, gridWithObs);

            // Execute FFRFF
            probe.execute(Command.F); // (0,1)
            probe.execute(Command.F); // (0,2)
            probe.execute(Command.R); // face EAST
            probe.execute(Command.F); // (1,2) is obstacle → stay at (0,2)
            probe.execute(Command.F); // (1,2) is still obstacle → stay at (0,2)

            assertEquals(new Position(0, 2), probe.getPosition());
            assertEquals(Direction.EAST, probe.getDirection());
        }

        @Test
        @DisplayName("Complex command sequence with multiple turns")
        void complexSequence_shouldNavigateCorrectly() {
            Probe probe = new Probe(new Position(0, 0), Direction.NORTH, grid);

            // F F R F F → should end at (2, 2) facing EAST
            probe.execute(Command.F); // (0,1)
            probe.execute(Command.F); // (0,2)
            probe.execute(Command.R); // face EAST
            probe.execute(Command.F); // (1,2)
            probe.execute(Command.F); // (2,2)

            assertEquals(new Position(2, 2), probe.getPosition());
            assertEquals(Direction.EAST, probe.getDirection());
            assertEquals(5, probe.getVisitedPositions().size());
        }
    }
}
