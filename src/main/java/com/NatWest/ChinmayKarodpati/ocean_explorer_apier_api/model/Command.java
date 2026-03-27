package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.model;

public enum Command {

    F, // Forward
    B, // Backward
    L, // Turn Left
    R; // Turn Right

    public static Command fromChar(char ch) {
        return switch (Character.toUpperCase(ch)) {
            case 'F' -> F;
            case 'B' -> B;
            case 'L' -> L;
            case 'R' -> R;
            default -> throw new IllegalArgumentException(
                    "Invalid command character: '" + ch + "'. Valid commands are: F, B, L, R");
        };
    }
}
