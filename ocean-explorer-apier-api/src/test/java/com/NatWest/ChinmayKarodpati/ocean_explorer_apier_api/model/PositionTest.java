package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Test suite for the Position object.
// Ensures that coordinates don't mutate unexpectedly and movement correctly calculates new positions.
class PositionTest {

    @Test
    @DisplayName("Should store and return correct coordinates")
    void constructor_shouldStoreCoordinates() {
        Position pos = new Position(3, 5);
        assertEquals(3, pos.getX());
        assertEquals(5, pos.getY());
    }

    @Test
    @DisplayName("move() should return a new Position with applied deltas")
    void move_shouldReturnNewPositionWithDeltas() {
        Position original = new Position(1, 1);
        Position moved = original.move(2, -1);

        assertEquals(3, moved.getX());
        assertEquals(0, moved.getY());
    }

    @Test
    @DisplayName("move() should not modify the original position (immutability)")
    void move_shouldNotModifyOriginal() {
        Position original = new Position(1, 1);
        original.move(2, 3);

        // Original should be unchanged
        assertEquals(1, original.getX());
        assertEquals(1, original.getY());
    }

    @Test
    @DisplayName("Equal positions should be equal and have same hashCode")
    void equals_shouldBeTrueForSameCoordinates() {
        Position a = new Position(2, 3);
        Position b = new Position(2, 3);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("Different positions should not be equal")
    void equals_shouldBeFalseForDifferentCoordinates() {
        Position a = new Position(2, 3);
        Position b = new Position(3, 2);

        assertNotEquals(a, b);
    }

    @Test
    @DisplayName("Position should not equal null or other types")
    void equals_shouldHandleNullAndDifferentTypes() {
        Position pos = new Position(1, 1);
        assertNotEquals(null, pos);
        assertNotEquals("not a position", pos);
    }

    @Test
    @DisplayName("toString should contain coordinates")
    void toString_shouldContainCoordinates() {
        Position pos = new Position(4, 7);
        String str = pos.toString();
        assertTrue(str.contains("4"));
        assertTrue(str.contains("7"));
    }
}
