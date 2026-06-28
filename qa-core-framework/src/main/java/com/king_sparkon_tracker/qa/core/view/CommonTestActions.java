package com.king_sparkon_tracker.qa.core.view;

import com.king_sparkon_tracker.qa.core.model.TestStepModel;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BooleanSupplier;

public interface CommonTestActions {

    default TestStepModel step(int order, String action, String expectedResult) {
        return new TestStepModel(order, action, expectedResult);
    }

    default List<TestStepModel> noStepsDefined() {
        return List.of(step(1, "Execute test logic", "Test should complete successfully"));
    }

    default void verifyTrue(boolean condition, String failureMessage) {
        if (!condition) {
            throw new AssertionError(failureMessage);
        }
    }

    default void verifyNotBlank(String value, String fieldName) {
        verifyTrue(value != null && !value.isBlank(), fieldName + " must not be blank");
    }

    default void runStep(TestStepModel step, Runnable action) {
        Objects.requireNonNull(step, "step is required");
        Objects.requireNonNull(action, "action is required");
        action.run();
    }

    default <T> T runStep(TestStepModel step, Callable<T> action) {
        Objects.requireNonNull(step, "step is required");
        Objects.requireNonNull(action, "action is required");
        try {
            return action.call();
        } catch (Exception exception) {
            throw new IllegalStateException("Step failed: " + step.order() + " - " + step.action(), exception);
        }
    }

    default void waitUntil(String description, BooleanSupplier condition, int maxAttempts, long sleepMillis) {
        Objects.requireNonNull(condition, "condition is required");
        int attempts = Math.max(1, maxAttempts);
        long delay = Math.max(0, sleepMillis);
        for (int i = 1; i <= attempts; i++) {
            if (condition.getAsBoolean()) {
                return;
            }
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Interrupted while waiting for " + description, exception);
                }
            }
        }
        throw new AssertionError("Condition was not met: " + description);
    }
}
