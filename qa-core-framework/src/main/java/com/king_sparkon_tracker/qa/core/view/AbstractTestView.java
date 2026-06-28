package com.king_sparkon_tracker.qa.core.view;

import com.king_sparkon_tracker.qa.core.model.TestCaseModel;
import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestStepModel;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTestView implements TestBuilder, CommonTestActions {

    private final TestCaseModel testCase;

    protected AbstractTestView(TestCaseModel testCase) {
        this.testCase = testCase;
    }

    protected TestCaseModel testCase() {
        return testCase;
    }

    @Override
    public final TestExecutionResponse buildTest() {
        long start = System.nanoTime();
        try {
            beforeEndToEndSteps();
            String passedDescription = executeTestLogic();
            afterEndToEndSteps();
            return TestExecutionResponse.passed(testCase, elapsedMs(start), enrichDescription(passedDescription));
        } catch (Throwable throwable) {
            return TestExecutionResponse.failed(testCase, elapsedMs(start), enrichDescription(failureDescription()), throwable);
        }
    }

    protected List<TestStepModel> endToEndSteps() {
        return noStepsDefined();
    }

    protected void beforeEndToEndSteps() {
        // Hook for views that need setup before running E2E steps.
    }

    protected void afterEndToEndSteps() {
        // Hook for views that need cleanup or final checks after E2E steps.
    }

    protected abstract String executeTestLogic() throws Exception;

    protected String failureDescription() {
        return "Test case failed: " + testCase.name();
    }

    protected String stepsAsText() {
        return endToEndSteps().stream()
                .map(step -> step.order() + ". " + step.action() + " -> " + step.expectedResult())
                .collect(Collectors.joining(" | "));
    }

    private String enrichDescription(String description) {
        String safeDescription = description == null ? "" : description;
        String steps = stepsAsText();
        if (steps.isBlank()) {
            return safeDescription;
        }
        return safeDescription + " | E2E Steps: " + steps;
    }

    private long elapsedMs(long startNano) {
        return Math.max(1, (System.nanoTime() - startNano) / 1_000_000);
    }
}
