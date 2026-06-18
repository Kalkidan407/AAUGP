package com.example.aaugp.dto.health;

import java.time.Instant;

public record HealthResponse(
        String status,
        String service,
        Instant timestamp) {
}
