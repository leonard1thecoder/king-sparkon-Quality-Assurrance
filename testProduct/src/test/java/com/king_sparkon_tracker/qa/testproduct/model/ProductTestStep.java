package com.king_sparkon_tracker.qa.testproduct.model;

public record ProductTestStep(
        String stepId,
        String testCaseId,
        int order,
        String actor,
        String action,
        String target,
        String testData,
        String expectedResult
) {
    public ProductTestStep {
        if (stepId == null || stepId.isBlank()) {
            throw new IllegalArgumentException("Product test step id is required");
        }
        if (testCaseId == null || testCaseId.isBlank()) {
            throw new IllegalArgumentException("Product test case id is required");
        }
        if (order < 1) {
            throw new IllegalArgumentException("Product test step order must be positive");
        }
        actor = actor == null || actor.isBlank() ? "QA_AUTOMATION" : actor;
        action = action == null ? "" : action;
        target = target == null ? "" : target;
        testData = testData == null ? "" : testData;
        expectedResult = expectedResult == null ? "" : expectedResult;
    }
}
