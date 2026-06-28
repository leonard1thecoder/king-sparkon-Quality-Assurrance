package com.king_sparkon_tracker.qa.testproduct.view;

import com.king_sparkon_tracker.qa.core.model.TestCaseModel;
import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.view.TestBuilder;
import com.king_sparkon_tracker.qa.testproduct.model.ProductTestCaseDefinition;
import com.king_sparkon_tracker.qa.testproduct.model.ProductTestStep;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public class ProductTestBuilder implements TestBuilder {

    private final ProductTestCaseDefinition testCase;
    private final List<ProductTestStep> steps;

    public ProductTestBuilder(ProductTestCaseDefinition testCase, List<ProductTestStep> steps) {
        this.testCase = testCase;
        this.steps = steps == null ? List.of() : List.copyOf(steps);
    }

    @Override
    public TestExecutionResponse buildTest() {
        Instant startedAt = Instant.now();
        TestCaseModel model = new TestCaseModel(
                testCase.id(),
                testCase.scenario(),
                testCase.name(),
                testCase.description()
        );

        try {
            validateDefinition();
            long durationMs = Math.max(1, Duration.between(startedAt, Instant.now()).toMillis());
            return TestExecutionResponse.passed(
                    model,
                    durationMs,
                    "Product QA definition is complete with " + matchingSteps().size() + " executable step definitions."
            );
        } catch (RuntimeException exception) {
            long durationMs = Math.max(1, Duration.between(startedAt, Instant.now()).toMillis());
            return TestExecutionResponse.failed(model, durationMs, "Product QA definition failed validation.", exception);
        }
    }

    public List<ProductTestStep> matchingSteps() {
        return steps.stream()
                .filter(step -> step.testCaseId().equals(testCase.id()))
                .sorted(Comparator.comparingInt(ProductTestStep::order))
                .toList();
    }

    private void validateDefinition() {
        if (testCase.expectedResult().isBlank()) {
            throw new IllegalArgumentException(testCase.id() + " must define an expected result");
        }
        if (matchingSteps().isEmpty()) {
            throw new IllegalArgumentException(testCase.id() + " must have at least one step in product-test-steps.scv");
        }
        boolean hasExpectedOutcome = matchingSteps().stream().anyMatch(step -> !step.expectedResult().isBlank());
        if (!hasExpectedOutcome) {
            throw new IllegalArgumentException(testCase.id() + " must have at least one step-level expected result");
        }
    }
}
