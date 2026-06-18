package com.example.aaugp.controller;

import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aaugp.dto.health.HealthResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "0. Health")
public class Health {

    @GetMapping("/api/health")
    @Operation(summary = "Check whether the API is reachable")
    public HealthResponse health() {
        return new HealthResponse("ok", "aaugp-api", Instant.now());
    }
}
