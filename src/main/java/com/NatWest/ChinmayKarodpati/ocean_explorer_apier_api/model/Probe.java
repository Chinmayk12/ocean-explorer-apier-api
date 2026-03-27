package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Probe {

    @Getter
    private Position position;
    @Getter
    private Direction direction;
    private final Grid grid;
    private final List<Position> visitedPositions;

//     Creates a new Probe on the given grid.
//     @param position  starting position
//     @param direction starting direction
//     @param grid the grid the probe operates on
//     @throws IllegalArgumentException if the starting position is not navigable
    public Probe(Position position, Direction direction, Grid grid) {
        if (!grid.isNavigable(position)) {
            throw new IllegalArgumentException(
                    "Starting position " + position + " is not navigable (out of bounds or obstacle)");
        }
        this.position = position;
        this.direction = direction;
        this.grid = grid;
        this.visitedPositions = new ArrayList<>();
        this.visitedPositions.add(position); // Record the starting position
    }

    //Return an unmodifiable view of all visited positions.
    public List<Position> getVisitedPositions() {
        return Collections.unmodifiableList(visitedPositions);
    }

    public void execute(Command command) {
        switch (command) {
            case F -> moveForward();
            case B -> moveBackward();
            case L -> turnLeft();
            case R -> turnRight();
        }
    }

     //Moves the probe one step forward in its current direction.
     //If the target position is not navigable, the probe stays put.
    private void moveForward() {
        Position newPosition = position.move(direction.getDx(), direction.getDy());
        attemptMoveTo(newPosition);
    }


     //Moves the probe one step backward (opposite of current direction).
     //If the target position is not navigable, the probe stays put.
    private void moveBackward() {
        Position newPosition = position.move(-direction.getDx(), -direction.getDy());
        attemptMoveTo(newPosition);
    }


    //Attempts to move the probe to a new position.
    //Only succeeds if the position is navigable on the grid ,@param newPosition the target position
    private void attemptMoveTo(Position newPosition) {
        if (grid.isNavigable(newPosition)) {
            this.position = newPosition;
            this.visitedPositions.add(newPosition);
        }
        // If not navigable, probe stays at current position (silently ignored)
    }

    //Turns the probe 90° counter-clockwise.
    private void turnLeft() {
        this.direction = direction.turnLeft();
    }

    // Turns the probe 90° clockwise.
    private void turnRight() {
        this.direction = direction.turnRight();
    }
}
