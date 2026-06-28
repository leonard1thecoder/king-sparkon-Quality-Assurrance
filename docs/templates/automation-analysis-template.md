# Automation Analysis Template

Use this template before creating or implementing a feature. It decides what should be automated, what should remain manual, and which QA module owns the test.

## Feature summary

| Field | Value |
| --- | --- |
| Feature ID | `KST-FEATURE-000` |
| Feature name |  |
| Release target | `0.1 / 0.2 / 0.3 / 0.4 / 0.5 / 1.0` |
| Epic |  |
| Owner |  |
| Business value |  |
| Risk level | `Low / Medium / High / Critical` |
| Dependencies |  |

## Automation decision table

| Test Case ID | Test Case Scenario | Expected Result | Automatable / Manual | Reason | Automation Layer | Manual Owner | Test Data Needed | Evidence Required | Priority | Status |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `TC-001` | Happy path | User completes the flow successfully | Automatable | Stable inputs and predictable result | Selenium / REST Assured / Appium / JMeter |  | Seeded user/product/barcode | HTML report + screenshot | P0 | Planned |
| `TC-002` | Payment provider behavior | External provider returns payment confirmation | Hybrid | API can be tested in sandbox, final finance review is manual | REST Assured + Manual | Owner/QA | Sandbox payment keys | API report + manual sign-off | P0 | Planned |
| `TC-003` | Visual polish review | Designer checks spacing, brand quality, and professional feel | Manual | Human judgment required | Manual | Product/Owner | Browser/device access | Screenshot + checklist | P1 | Planned |
| `TC-004` | Security header check | Page/API has safe baseline headers | Automatable | ZAP can detect passive issues | Security/ZAP |  | Running app URL | ZAP report | P1 | Planned |

## Automation classification rules

| Classification | Use when |
| --- | --- |
| Automatable | Result is deterministic, repeatable, and safe to run often. |
| Manual | Requires human judgment, legal/finance review, visual approval, or live provider confirmation. |
| Hybrid | Automation can verify most of it but manual sign-off is still required. |
| Not Ready | Feature is missing stable API, UI locator, test data, sandbox provider, or acceptance criteria. |

## End-to-end steps for View override

Every View should override:

```java
@Override
protected List<TestStepModel> endToEndSteps() {
    return List.of(
            step(1, "Open target screen/API", "Target is reachable"),
            step(2, "Prepare seeded test data", "Data is available"),
            step(3, "Execute user action", "System responds"),
            step(4, "Validate expected result", "Scenario passes"),
            step(5, "Capture evidence", "HTML report/screenshot is produced")
    );
}
```

## Common methods to reuse

| Method | Purpose |
| --- | --- |
| `step(order, action, expectedResult)` | Builds a reusable E2E step model. |
| `verifyTrue(condition, message)` | Common assertion helper. |
| `verifyNotBlank(value, fieldName)` | Guards required values. |
| `runStep(step, Runnable)` | Wraps a step with consistent error context. |
| `runStep(step, Callable<T>)` | Runs a step and returns data. |
| `waitUntil(description, condition, maxAttempts, sleepMillis)` | Reusable wait helper for eventual conditions. |

## Feature readiness checklist

| Check | Yes/No | Notes |
| --- | --- | --- |
| Acceptance criteria are clear |  |  |
| Test data exists or can be seeded |  |  |
| UI has stable locators or accessibility IDs |  |  |
| API endpoint is documented |  |  |
| Negative scenarios are defined |  |  |
| Security risk is known |  |  |
| Payment/messaging provider is sandboxed |  |  |
| Screenshot evidence is needed |  |  |
| Automation layer is selected |  |  |
| Manual sign-off owner is assigned |  |  |

## Feature creation output

When this analysis is complete, create:

1. Feature issue/backlog item.
2. `.svc` scenario rows.
3. Model entity if needed.
4. View test logic with `endToEndSteps()` override.
5. Controller `@Test` file.
6. HTML report evidence.
7. Screenshot/Supabase evidence where required.
