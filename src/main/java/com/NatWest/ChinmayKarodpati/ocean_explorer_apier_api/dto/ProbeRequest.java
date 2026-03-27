package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProbeRequest {

    @NotNull(message = "startX is required")
    @Min(value = 0, message = "startX must be non-negative")
    private Integer startX;

    @NotNull(message = "startY is required")
    @Min(value = 0, message = "startY must be non-negative")
    private Integer startY;

    @NotBlank(message = "direction is required")
    @Pattern(regexp = "NORTH|SOUTH|EAST|WEST",
            message = "direction must be one of: NORTH, SOUTH, EAST, WEST")
    private String direction;

    @NotNull(message = "commands is required")
    @Pattern(regexp = "[FBLRfblr]*",
            message = "commands must only contain characters: F, B, L, R")
    private String commands;

    @NotNull(message = "gridSize is required")
    @Min(value = 1, message = "gridSize must be at least 1")
    private Integer gridSize;

    //List of obstacle coordinates as [x, y] pairs. Nullable (no obstacles).
    private List<int[]> obstacles;

}
