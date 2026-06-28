package com.king_sparkon_tracker.qa.core.model;

public record TestCaseModel(
        String id,
        String scenario,
        String name,
        String description
) {
    public TestCaseModel {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Test case id is required");
        }
        if (scenario == null || scenario.isBlank()) {
            throw new IllegalArgumentException("Test case scenario is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Test case name is required");
        }
        description = description == null ? "" : description;
    }
}
