package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Ocean Explorer API");
        response.put("version", "0.0.1-SNAPSHOT");
        response.put("status", "running");
        response.put("endpoints", Map.of(
            "probe", "/api/probe/move",
            "health", "/actuator/health",
            "info", "/actuator/info"
        ));
        return ResponseEntity.ok(response);
    }
}
