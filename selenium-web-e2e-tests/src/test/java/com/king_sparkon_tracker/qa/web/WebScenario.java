package com.king_sparkon_tracker.qa.web;

public record WebScenario(
        String id,
        String name,
        String path,
        String expectedContent,
        boolean requiresAuth
) {
}
