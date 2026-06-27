package com.king_sparkon_tracker.qa.core.model;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public record TestSuiteReport(
        String suiteName,
        Instant startedAt,
        Instant completedAt,
        List<TestExecutionResponse> responses
) {
    public TestSuiteReport {
        suiteName = suiteName == null || suiteName.isBlank() ? "King Sparkon QA Report" : suiteName;
        startedAt = startedAt == null ? Instant.now() : startedAt;
        completedAt = completedAt == null ? Instant.now() : completedAt;
        responses = responses == null ? List.of() : List.copyOf(responses);
    }

    public int totalTests() {
        return responses.size();
    }

    public long passedTests() {
        return responses.stream().filter(TestExecutionResponse::passed).count();
    }

    public long failedTests() {
        return responses.stream().filter(TestExecutionResponse::failed).count();
    }

    public long skippedTests() {
        return responses.stream().filter(response -> response.status() == TestStatus.SKIPPED).count();
    }

    public long totalDurationMs() {
        long sum = responses.stream().mapToLong(TestExecutionResponse::durationMs).sum();
        long wallClock = Duration.between(startedAt, completedAt).toMillis();
        return Math.max(sum, wallClock);
    }

    public double passPercentage() {
        if (totalTests() == 0) {
            return 0;
        }
        return (passedTests() * 100.0) / totalTests();
    }

    public double failPercentage() {
        if (totalTests() == 0) {
            return 0;
        }
        return (failedTests() * 100.0) / totalTests();
    }

    public String passPercentageLabel() {
        return String.format("%.2f%%", passPercentage());
    }

    public String failPercentageLabel() {
        return String.format("%.2f%%", failPercentage());
    }
}
