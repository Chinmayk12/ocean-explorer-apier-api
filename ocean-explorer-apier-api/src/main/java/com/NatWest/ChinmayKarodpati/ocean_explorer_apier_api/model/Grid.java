package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.model;

import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Getter
public class Grid {

    private final int size;
    private final Set<Position> obstacles;

     //@param size      the grid dimension (grid spans 0 to size-1 on both axes)
     //@param obstacles set of blocked coordinates
     //@throws IllegalArgumentException if size is not positive
    public Grid(int size, Set<Position> obstacles) {
        if (size <= 0) {
            throw new IllegalArgumentException("Grid size must be positive, got: " + size);
        }
        this.size = size;
        // Defensive copy to prevent external mutation
        this.obstacles = obstacles != null
                ? Collections.unmodifiableSet(new HashSet<>(obstacles))
                : Collections.emptySet();
    }

     //Checks if a position is within the grid boundaries.
     //@param position the position to validate
     // @return true if the position is within bounds
    public boolean isWithinBounds(Position position) {
        return position.getX() >= 0 && position.getX() < size
                && position.getY() >= 0 && position.getY() < size;
    }

    //Checks if a position is blocked by an obstacle
    //@param position the position to check
    //@return true if the position contains an obstacle
    public boolean isObstacle(Position position) {
        return obstacles.contains(position);
    }

    //Checks if a position is a valid navigable location
    // i.e., within bounds AND not an obstacle.
    //@param position the position to validate
    //return true if the probe can move to this position
    public boolean isNavigable(Position position) {
        return isWithinBounds(position) && !isObstacle(position);
    }
}
