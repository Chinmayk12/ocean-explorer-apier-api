package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.service;

import com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.dto.ProbeRequest;
import com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.dto.ProbeResponse;
import com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Service layer for orchestrating the overall logic around moving the probe.
// It takes incoming API requests, turns them into internal domain models like Grid and Probe,
// tells the probe to execute the commands, and returns the final state back as an API response.
// Note: Handing actual movement and turning logic is left strictly to the Probe and Direction models themselves.
@Service
public class ProbeService {

    private static final Logger log = LoggerFactory.getLogger(ProbeService.class);

    // Kicks off the probe movement based on the validated incoming request.
    // Returns the final position, the direction it ended up facing, and every spot it touched along the way.
    public ProbeResponse processCommands(ProbeRequest request) {
        log.info("Processing probe commands — start=({},{}), direction={}, commands='{}', gridSize={}",
                request.getStartX(), request.getStartY(), request.getDirection(),
                request.getCommands(), request.getGridSize());

        // 1. Build the obstacle set from request
        Set<Position> obstacles = parseObstacles(request.getObstacles());
        log.debug("Parsed {} obstacles", obstacles.size());

        // 2. Create the grid
        Grid grid = new Grid(request.getGridSize(), obstacles);

        // 3. Parse direction
        Direction direction = Direction.valueOf(request.getDirection().toUpperCase());

        // 4. Create the probe at its starting position
        Position startPosition = new Position(request.getStartX(), request.getStartY());
        Probe probe = new Probe(startPosition, direction, grid);

        // 5. Parse and execute each command sequentially
        String commands = request.getCommands();
        if (commands != null && !commands.isEmpty()) {
            for (char ch : commands.toCharArray()) {
                Command command = Command.fromChar(ch);
                probe.execute(command);
                log.debug("Executed command '{}' → position={}, direction={}",
                        ch, probe.getPosition(), probe.getDirection());
            }
        }

        // 6. Build the response
        ProbeResponse response = buildResponse(probe);
        log.info("Probe finished — final=({},{}), direction={}",
                response.getFinalX(), response.getFinalY(), response.getDirection());

        return response;
    }

    // Helper to safely parse raw lists of [x, y] integer arrays into a Set of Position objects.
    // Throws an error early if any obstacle is malformed or missing coordinates.
    private Set<Position> parseObstacles(List<int[]> obstacleList) {
        if (obstacleList == null || obstacleList.isEmpty()) {
            return new HashSet<>();
        }

        Set<Position> obstacles = new HashSet<>();
        for (int[] coords : obstacleList) {
            if (coords == null || coords.length != 2) {
                throw new IllegalArgumentException(
                        "Each obstacle must be a coordinate pair [x, y]");
            }
            obstacles.add(new Position(coords[0], coords[1]));
        }
        return obstacles;
    }

    // Helper to map the probe's internal domain state back to something the API client understands.
    private ProbeResponse buildResponse(Probe probe) {
        List<int[]> visitedCoords = probe.getVisitedPositions().stream()
                .map(pos -> new int[]{pos.getX(), pos.getY()})
                .collect(Collectors.toList());

        return new ProbeResponse(
                probe.getPosition().getX(),
                probe.getPosition().getY(),
                probe.getDirection().name(),
                visitedCoords
        );
    }
}
