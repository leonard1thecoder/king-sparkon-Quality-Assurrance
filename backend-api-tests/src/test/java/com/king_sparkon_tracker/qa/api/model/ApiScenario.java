package com.king_sparkon_tracker.qa.api.model;

public record ApiScenario(
        String id,
        String name,
        String method,
        String path,
        String body,
        int expectedStatus,
        boolean requiresAuth
) {
}
