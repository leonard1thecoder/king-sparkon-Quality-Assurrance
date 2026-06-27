package com.king_sparkon_tracker.qa.core.model;

import java.time.Instant;

public record TestExecutionResponse(
        String testCaseId,
        String scenario,
        String testCaseName,
        String description,
        TestStatus status,
        long durationMs,
        String errorMessage,
        Instant executedAt
) {
    public TestExecutionResponse {
        if (testCaseId == null || testCaseId.isBlank()) {
            throw new IllegalArgumentException("testCaseId is required");
        }
        if (scenario == null || scenario.isBlank()) {
            throw new IllegalArgumentException("scenario is required");
        }
        if (testCaseName == null || testCaseName.isBlank()) {
            throw new IllegalArgumentException("testCaseName is required");
        }
        description = description == null ? "" : description;
        status = status == null ? TestStatus.FAILED : status;
        errorMessage = errorMessage == null ? "" : errorMessage;
        executedAt = executedAt == null ? Instant.now() : executedAt;
    }

    public boolean passed() {
        return status == TestStatus.PASSED;
    }

    public boolean failed() {
        return status == TestStatus.FAILED;
    }

    public static TestExecutionResponse passed(TestCaseModel testCase, long durationMs, String passedDescription) {
        return new TestExecutionResponse(
                testCase.id(),
                testCase.scenario(),
                testCase.name(),
                passedDescription == null || passedDescription.isBlank() ? testCase.description() : passedDescription,
                TestStatus.PASSED,
                durationMs,
                "",
                Instant.now()
        );
    }

    public static TestExecutionResponse failed(TestCaseModel testCase, long durationMs, String failedDescription, Throwable throwable) {
        String error = throwable == null ? failedDescription : throwable.getMessage();
        return new TestExecutionResponse(
                testCase.id(),
                testCase.scenario(),
                testCase.name(),
                failedDescription == null || failedDescription.isBlank() ? testCase.description() : failedDescription,
                TestStatus.FAILED,
                durationMs,
                error == null ? "Unknown test failure" : error,
                Instant.now()
        );
    }
}
