package com.king_sparkon_tracker.qa.web.model;

public record WebScenarioModel(
        String id,
        String name,
        String path,
        String expectedContent,
        boolean requiresAuth
) {
}
