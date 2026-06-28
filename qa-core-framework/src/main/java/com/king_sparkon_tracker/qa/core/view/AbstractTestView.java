package com.king_sparkon_tracker.qa.core.view;

import com.king_sparkon_tracker.qa.core.model.TestCaseModel;
import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;

public abstract class AbstractTestView implements TestBuilder {

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
            String passedDescription = executeTestLogic();
            return TestExecutionResponse.passed(testCase, elapsedMs(start), passedDescription);
        } catch (Throwable throwable) {
            return TestExecutionResponse.failed(testCase, elapsedMs(start), failureDescription(), throwable);
        }
    }

    protected abstract String executeTestLogic() throws Exception;

    protected String failureDescription() {
        return "Test case failed: " + testCase.name();
    }

    private long elapsedMs(long startNano) {
        return Math.max(1, (System.nanoTime() - startNano) / 1_000_000);
    }
}
