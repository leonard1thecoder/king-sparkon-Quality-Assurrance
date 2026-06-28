package com.king_sparkon_tracker.qa.backend.model;

public record BusinessRestStep(
        String stepId,
        String testCaseId,
        int order,
        String actor,
        String action,
        String target,
        String testData,
        String expectedResult
) {
    public BusinessRestStep {
        if (stepId == null || stepId.isBlank()) {
            throw new IllegalArgumentException("REST step id is required");
        }
        if (testCaseId == null || testCaseId.isBlank()) {
            throw new IllegalArgumentException(stepId + " must link to a testCaseId");
        }
        if (order < 1) {
            throw new IllegalArgumentException(stepId + " must have a positive order");
        }
        actor = actor == null || actor.isBlank() ? "REST_ASSURED" : actor;
        action = action == null ? "" : action;
        target = target == null ? "" : target;
        testData = testData == null ? "" : testData;
        expectedResult = expectedResult == null ? "" : expectedResult;
    }
}
