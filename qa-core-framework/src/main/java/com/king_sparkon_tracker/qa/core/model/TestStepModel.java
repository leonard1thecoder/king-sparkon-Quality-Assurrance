package com.king_sparkon_tracker.qa.core.model;

public record TestStepModel(
        int order,
        String action,
        String expectedResult
) {
    public TestStepModel {
        if (order <= 0) {
            throw new IllegalArgumentException("Step order must be greater than zero");
        }
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Step action is required");
        }
        expectedResult = expectedResult == null ? "" : expectedResult.trim();
    }
}
