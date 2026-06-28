package com.king_sparkon_tracker.qa.testproduct.model;

public record ProductTestCaseDefinition(
        String id,
        String scenario,
        String name,
        String description,
        String priority,
        String feature,
        String expectedResult,
        String tags
) {
    public ProductTestCaseDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Product test id is required");
        }
        if (scenario == null || scenario.isBlank()) {
            throw new IllegalArgumentException("Product scenario is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product test name is required");
        }
        description = description == null ? "" : description;
        priority = priority == null || priority.isBlank() ? "P2" : priority;
        feature = feature == null || feature.isBlank() ? "Product" : feature;
        expectedResult = expectedResult == null ? "" : expectedResult;
        tags = tags == null ? "" : tags;
    }
}
