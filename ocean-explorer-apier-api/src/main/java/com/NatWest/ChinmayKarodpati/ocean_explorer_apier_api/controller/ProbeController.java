package com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.controller;

import com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.dto.ProbeRequest;
import com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.dto.ProbeResponse;
import com.NatWest.ChinmayKarodpati.ocean_explorer_apier_api.service.ProbeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/probe")
public class ProbeController {

    private static final Logger log = LoggerFactory.getLogger(ProbeController.class);

    private final ProbeService probeService;

    public ProbeController(ProbeService probeService) {
        this.probeService = probeService;
    }


     //Processes a sequence of commands for the submersible probe.
     //@param request validated request containing start position, direction,
     //commands, grid size, and optional obstacles
     //@return response with final position, direction, and visited path

    @PostMapping("/move")
    public ResponseEntity<ProbeResponse> moveProbe(@Valid @RequestBody ProbeRequest request) {
        log.info("Received probe move request");
        ProbeResponse response = probeService.processCommands(request);
        return ResponseEntity.ok(response);
    }
}
