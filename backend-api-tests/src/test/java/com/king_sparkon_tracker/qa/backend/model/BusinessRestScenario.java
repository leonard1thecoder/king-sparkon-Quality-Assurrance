package com.king_sparkon_tracker.qa.backend.model;

public record BusinessRestScenario(
        String id,
        String domain,
        String caseType,
        String name,
        String method,
        String path,
        String body,
        String expectedStatus,
        boolean requiresAuth,
        String priority,
        String expectedBodyContains
) {
    public BusinessRestScenario {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("REST scenario id is required");
        }
        if (domain == null || domain.isBlank()) {
            throw new IllegalArgumentException(id + " must define a domain");
        }
        if (caseType == null || caseType.isBlank()) {
            throw new IllegalArgumentException(id + " must define POSITIVE or NEGATIVE caseType");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(id + " must define a test name");
        }
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException(id + " must define an HTTP method");
        }
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException(id + " must define an endpoint path");
        }
        if (expectedStatus == null || expectedStatus.isBlank()) {
            throw new IllegalArgumentException(id + " must define expectedStatus");
        }
        domain = domain.trim().toUpperCase();
        caseType = caseType.trim().toUpperCase();
        method = method.trim().toUpperCase();
        body = body == null ? "" : body.trim();
        priority = priority == null || priority.isBlank() ? "P2" : priority.trim().toUpperCase();
        expectedBodyContains = expectedBodyContains == null ? "" : expectedBodyContains.trim();
    }

    public boolean positive() {
        return "POSITIVE".equals(caseType);
    }

    public boolean negative() {
        return "NEGATIVE".equals(caseType);
    }
}
