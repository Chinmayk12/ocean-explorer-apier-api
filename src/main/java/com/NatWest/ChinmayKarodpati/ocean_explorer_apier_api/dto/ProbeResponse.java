package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// Simple object that holds the final output of the probe's journey to send back to the user.
@Setter
@Getter
public class ProbeResponse {

    private int finalX;
    private int finalY;
    private String direction;
    private List<int[]> visited;

    public ProbeResponse() {
    }

    public ProbeResponse(int finalX, int finalY, String direction, List<int[]> visited) {
        this.finalX = finalX;
        this.finalY = finalY;
        this.direction = direction;
        this.visited = visited;
    }

}
