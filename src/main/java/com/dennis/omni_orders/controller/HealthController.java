package com.dennis.omni_orders.controller;

// Swagger annotation: used to hide endpoints from Swagger UI
import io.swagger.v3.oas.annotations.Hidden;

// Swagger annotation: used to describe individual endpoints
import io.swagger.v3.oas.annotations.Operation;

// Swagger annotation: used to group endpoints under a named section
import io.swagger.v3.oas.annotations.tags.Tag;

// Spring annotation: marks this class as a REST controller
// This means all methods return data (JSON, String, etc.) instead of views
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HealthController
 *
 * Purpose:
 * This controller provides simple endpoints to verify that the API is running.
 *
 * Why this exists:
 * - Used by developers to confirm the backend is alive
 * - Used by monitoring tools (future production use)
 * - Provides a quick sanity check endpoint
 *
 * Swagger:
 * This controller is grouped under "System Health" in Swagger UI
 */
@Tag(name = "System Health", description = "Endpoints for API health and uptime checks")
@RestController
public class HealthController {

    /**
     * GET /health
     *
     * Purpose:
     * Returns a structured JSON response indicating that the API is running.
     *
     * Why JSON instead of plain text:
     * - More professional
     * - Easier for monitoring systems to parse
     * - Can be extended later (uptime, version, etc.)
     *
     * Swagger:
     * - Appears in Swagger UI with description and summary
     */
    @Operation(
            summary = "Health check",
            description = "Returns the current health status of the Omni Orders API."
    )
    @GetMapping("/health")
    public Map<String, Object> health() {

        // LinkedHashMap is used to preserve insertion order (cleaner JSON output)
        Map<String, Object> response = new LinkedHashMap<>();

        // Basic health indicator
        response.put("status", "UP");

        // Identifies which service is running
        response.put("service", "Omni Orders API");

        // Adds current timestamp (useful for debugging and monitoring)
        response.put("timestamp", LocalDateTime.now().toString());

        return response;
    }

    /**
     * GET /
     *
     * Purpose:
     * Simple root endpoint for quick browser checks.
     *
     * Why hidden:
     * - Not part of the "real" API
     * - Avoid cluttering Swagger UI
     *
     * Use case:
     * - Quick manual check: open browser and see if server responds
     */
    @Hidden
    @GetMapping("/")
    public String home() {
        return "Omni Orders API is running";
    }
}