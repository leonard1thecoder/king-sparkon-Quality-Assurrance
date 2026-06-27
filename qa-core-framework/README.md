# QA Core Framework

Shared MVC-style framework used by the King Sparkon QA modules.

## MVC convention

| Layer | Package | Responsibility |
| --- | --- | --- |
| Model | `model` | Test case entities, status, response objects, suite report objects. |
| View | `view` | Service-side test logic and report builders. Every test logic class should implement `TestBuilder`. |
| Controller | `controller` | JUnit `@Test` files that call the view layer and assert/report the result. |

## Build contract

Every view/service test class implements:

```java
public interface TestBuilder {
    TestExecutionResponse buildTest();
}
```

The controller receives the `TestExecutionResponse`, writes it to the HTML report, and asserts whether the test passed.

## HTML report

The report includes:

- Total duration.
- Total test cases.
- Passed count.
- Failed count.
- Overall test coverage/pass rate.
- Interactive pass/fail pie chart with hover percentages.
- Table with test case ID, scenario, test case name, description, status, duration, and error message.

Default output path:

```text
target/qa-report/index.html
```
