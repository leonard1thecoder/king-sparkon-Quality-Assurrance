package com.king_sparkon_tracker.qa.api.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record ApiScenario(
        String id,
        String name,
        String method,
        String path,
        String body,
        List<Integer> expectedStatuses,
        boolean requiresAuth
) {

    public ApiScenario {
        expectedStatuses = List.copyOf(Objects.requireNonNull(expectedStatuses, "expectedStatuses must not be null"));
        if (expectedStatuses.isEmpty()) {
            throw new IllegalArgumentException("expectedStatuses must contain at least one HTTP status code");
        }
    }

    public int expectedStatus() {
        return expectedStatuses.getFirst();
    }

    public boolean acceptsStatus(int actualStatus) {
        return expectedStatuses.contains(actualStatus);
    }

    public String expectedStatusesLabel() {
        return expectedStatuses.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}
