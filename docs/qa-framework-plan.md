# King Sparkon QA Framework Plan

## Modules

| Module | Testing layer | Main tool |
| --- | --- | --- |
| `backend-api-tests` | Backend API regression | REST Assured + JUnit 5 |
| `selenium-web-e2e-tests` | Website end-to-end | Selenium WebDriver + JUnit 5 |
| `security-tests` | Passive web/API security baseline | OWASP ZAP Baseline Scan |
| `appium-android-tests` | Android mobile automation | Appium Java Client + JUnit 5 |
| `jmeter-performance-tests` | Backend load/performance | Apache JMeter |

## Why this stack

- **REST Assured** is strong for Java API tests and keeps backend regression tests close to Spring Boot engineering style.
- **Selenium WebDriver** is the preferred choice here because you asked for Selenium driver and it supports local and remote browser execution.
- **OWASP ZAP Baseline** gives safe passive security checks for CI before deeper active security testing.
- **Appium** gives Android automation that can later target the barcode scanner mobile app.
- **JMeter** remains dedicated to performance, not functional UI testing.

## Test data strategy

`.svc` files are pipe-delimited scenario/data catalogs. Keep them readable and versioned.

Example:

```text
id|layer|scenario|priority|appArea|expectedResult
WEB-LOGIN-001|WEB|Login page renders|P0|Authentication|Email and password fields are visible
```

## Execution strategy

| Stage | Run |
| --- | --- |
| Pull request smoke | API public smoke, Selenium public page smoke |
| Nightly | Full API regression, Web E2E, ZAP baseline |
| Release candidate | API + Web + Android emulator + JMeter baseline |
| Controlled window | Stress, spike, active security scans |

## Non-negotiable QA rules

1. Tests must not depend on real customer data.
2. Payment/messaging tests must use sandbox providers.
3. Destructive flows need isolated seeded data.
4. UI tests should use stable attributes like `data-testid` instead of brittle CSS chains.
5. Every failed test should tell a developer exactly what broke.
