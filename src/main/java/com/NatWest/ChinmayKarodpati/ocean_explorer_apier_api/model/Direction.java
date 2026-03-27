package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.model;


import lombok.Getter;

@Getter
public enum Direction {

    NORTH(0, 1),
    EAST(1, 0),
    SOUTH(0, -1),
    WEST(-1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

     //Returns the direction after turning left (counter-clockwise).
     //NORTH → WEST → SOUTH → EAST → NORTH
     public Direction turnLeft() {
        return switch (this) {
            case NORTH -> WEST;
            case WEST -> SOUTH;
            case SOUTH -> EAST;
            case EAST -> NORTH;
        };
    }


    //Returns the direction after turning right (clockwise).
    //NORTH → EAST → SOUTH → WEST → NORTH
    public Direction turnRight() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }
}
