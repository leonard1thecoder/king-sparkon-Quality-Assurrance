# King Sparkon Quality Assurance

A professional QA automation workspace for **King Sparkon Tracker**.

This repository is structured as a **Maven multi-module MVC QA application** so backend API, web UI, Android, security, and performance test suites can live together without becoming a messy scripts folder.

## Current modules

| Module | Purpose |
| --- | --- |
| `qa-core-framework` | Shared MVC test models, test-builder view contract, locator framework, screenshot upload, response objects, E2E steps, automation analysis models, and HTML reporting. |
| `backend-api-tests` | REST Assured API regression tests for the King Sparkon Tracker backend. |
| `selenium-web-e2e-tests` | Selenium WebDriver end-to-end tests and screenshot capture for the Next.js website. |
| `security-tests` | OWASP ZAP baseline security scanning plus MVC readiness report. |
| `appium-android-tests` | Appium Android automation and screenshot capture for future barcode scanner Android development. |
| `jmeter-performance-tests` | Apache JMeter performance tests for backend health, API load, and barcode scan spike scenarios. |

## King Sparkon backlog and releases

The backlog is planned through release slices so features are created with QA evidence from day one.

| File | Purpose |
| --- | --- |
| `docs/releases/king-sparkon-release-roadmap.md` | Product vision, release roadmap, epics, evidence gates, and backlog workflow. |
| `docs/templates/automation-analysis-template.md` | Template for deciding automatable vs manual test cases before feature work starts. |
| `.github/ISSUE_TEMPLATE/feature-automation-analysis.md` | GitHub issue template for creating feature backlog items with automation analysis. |
| `test-assets/backlog/king-sparkon-backlog.csv` | Seed backlog list by release, epic, feature, priority, and automation layer. |

## MVC test structure

Each Java test module follows this structure:

| Layer | Folder/package | Responsibility |
| --- | --- | --- |
| Model | `model` | Test case entities, scenario data objects, automation-analysis models, and E2E step models. |
| View | `view` | Service-side logic that builds and executes test cases. View classes implement `TestBuilder` and override `endToEndSteps()`. |
| Controller | `controller` | JUnit `@Test` classes. Controllers call views, assert results, and write HTML reports. |

The shared view contract is:

```java
public interface TestBuilder {
    TestExecutionResponse buildTest();
}
```

`buildTest()` returns the response that is displayed in the HTML report.

Views can document the full journey by overriding:

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

Common methods are provided by `CommonTestActions`:

| Method | Purpose |
| --- | --- |
| `step(order, action, expectedResult)` | Builds a reusable E2E step. |
| `verifyTrue(condition, message)` | Common assertion helper. |
| `verifyNotBlank(value, fieldName)` | Guards required values. |
| `runStep(step, Runnable)` | Runs a step with shared error context. |
| `runStep(step, Callable<T>)` | Runs a step and returns data. |
| `waitUntil(description, condition, maxAttempts, sleepMillis)` | Reusable wait helper. |

## Locator framework

The framework supports locator-driven test actions from `.svc` files.

| Strategy | Selenium | Appium |
| --- | --- | --- |
| `id` | Yes | Yes |
| `name` | Yes | Yes |
| `css` | Yes | Yes where driver supports it |
| `class-name` | Yes | Yes |
| `tag-name` | Yes | Yes |
| `link-text` | Yes | Yes where driver supports it |
| `partial-link-text` | Yes | Yes where driver supports it |
| `xpath` | Yes | Yes |
| `full-xpath` | Yes | Yes |
| `js-path` | Yes | No for native Android screens |
| `accessibility-id` | No | Yes |
| `android-ui-automator` | No | Yes |
| `ios-predicate` | No | Added for future iOS support |
| `ios-class-chain` | No | Added for future iOS support |

Selenium screenshot locator file:

```text
selenium-web-e2e-tests/src/test/resources/scenarios/web-screenshot-locators.svc
```

Appium screenshot locator file:

```text
appium-android-tests/src/test/resources/scenarios/android-screenshot-locators.svc
```

Format:

```text
id|pagePath|strategy|locatorValue|expectedText|description
```

## Screenshot and Supabase upload

Selenium and Appium screenshot tests save PNG files locally first:

```text
target/qa-screenshots/*.png
```

When Supabase upload is enabled, screenshots are uploaded to Supabase Storage under:

```text
qa-screenshots/{testCaseId}/{fileName}.png
```

Supabase upload is disabled by default. Enable it with Maven properties or environment variables:

```bash
-Dsupabase.uploadEnabled=true \
-Dsupabase.url=https://YOUR_PROJECT.supabase.co \
-Dsupabase.bucket=qa-screenshots \
-Dsupabase.serviceRoleKey=YOUR_SERVICE_ROLE_KEY
```

Environment variable equivalents:

```text
SUPABASE_UPLOAD_ENABLED=true
SUPABASE_URL=https://YOUR_PROJECT.supabase.co
SUPABASE_BUCKET=qa-screenshots
SUPABASE_SERVICE_ROLE_KEY=YOUR_SERVICE_ROLE_KEY
```

Never commit Supabase keys. Use GitHub Actions secrets or local environment variables.

## HTML reports

Java controller tests write reports under each module:

```text
target/qa-report/*.html
```

The report contains:

- Total duration.
- Test case name field.
- Passed tests count.
- Failed tests count.
- Overall test coverage/pass rate.
- Interactive pie chart for passed vs failed.
- Hover tooltip showing passed/failed percentage.
- Result table with test case ID, scenario, test case name, description, status, duration, and error message.

## Default apps under test

| App | Default URL |
| --- | --- |
| Backend API | `http://localhost:8080` |
| Website frontend | `http://localhost:3000` |
| Appium server | `http://127.0.0.1:4723` |

## Scenario and app data files

You asked for `.svc` upload-style files for scenarios and app data. I interpreted `.svc` as **Scenario Value Catalog** files and added pipe-delimited `.svc` files that are easy to edit, commit, and load from tests.

| File | Purpose |
| --- | --- |
| `test-assets/scenarios/king-sparkon-test-cases.svc` | Central cross-platform test case inventory. |
| `test-assets/data/king-sparkon-app-data.svc` | Shared app data values for backend, web, and Android tests. |
| `backend-api-tests/src/test/resources/scenarios/backend-api-test-cases.svc` | API endpoint scenarios. |
| `selenium-web-e2e-tests/src/test/resources/scenarios/web-ui-test-cases.svc` | Website page and UI scenarios. |
| `appium-android-tests/src/test/resources/data/android-app-data.svc` | Android/Appium app identifiers and locator placeholders. |

## Run backend API tests

```bash
mvn -am -pl backend-api-tests test \
  -Dapi.baseUrl=http://localhost:8080 \
  -Dapi.authToken="YOUR_ACCESS_TOKEN"
```

Auth-required scenarios are reported as skipped when `api.authToken` is empty.

## Run Selenium website E2E and screenshots

```bash
mvn -am -pl selenium-web-e2e-tests test \
  -Dui.baseUrl=http://localhost:3000 \
  -Dui.browser=chrome \
  -Dui.headless=true
```

With Supabase upload:

```bash
mvn -am -pl selenium-web-e2e-tests test \
  -Dui.baseUrl=http://localhost:3000 \
  -Dsupabase.uploadEnabled=true \
  -Dsupabase.url=https://YOUR_PROJECT.supabase.co \
  -Dsupabase.bucket=qa-screenshots \
  -Dsupabase.serviceRoleKey=YOUR_SERVICE_ROLE_KEY
```

For Selenium Grid:

```bash
mvn -am -pl selenium-web-e2e-tests test \
  -Dui.baseUrl=http://localhost:3000 \
  -Dselenium.remoteUrl=http://localhost:4444/wd/hub
```

## Run Appium Android tests and screenshots

Start Appium first, then run:

```bash
mvn -am -pl appium-android-tests test \
  -Dappium.serverUrl=http://127.0.0.1:4723 \
  -Dappium.deviceName="Android Emulator" \
  -Dappium.platformVersion=15 \
  -Dappium.appPackage=com.kingsparkon.tracker \
  -Dappium.appActivity=.MainActivity
```

Or pass an APK path:

```bash
mvn -am -pl appium-android-tests test \
  -Dappium.app=/absolute/path/to/king-sparkon-tracker.apk
```

## Run security baseline scan

```bash
mvn -am -pl security-tests -Pzap-baseline verify \
  -Dsecurity.targetUrl=http://localhost:3000
```

## Run performance tests locally

```bash
mvn -pl jmeter-performance-tests verify \
  -DtargetProtocol=http \
  -DtargetHost=localhost \
  -DtargetPort=8080 \
  -Dthreads=10 \
  -DrampUpSeconds=30 \
  -DdurationSeconds=60
```

## GitHub Actions

| Workflow | Purpose |
| --- | --- |
| `QA Framework Build` | Compiles Java QA modules on PRs. |
| `QA API and Web Smoke` | Manual backend API + Selenium website smoke/screenshot run and uploads MVC HTML reports/screenshots. |
| `Security ZAP Baseline` | Manual OWASP ZAP passive baseline scan and uploads MVC/ZAP reports. |
| `JMeter Performance Tests` | Manual backend performance run. |

## Test strategy docs

- [`docs/releases/king-sparkon-release-roadmap.md`](docs/releases/king-sparkon-release-roadmap.md)
- [`docs/templates/automation-analysis-template.md`](docs/templates/automation-analysis-template.md)
- [`docs/screenshot-locator-supabase.md`](docs/screenshot-locator-supabase.md)
- [`docs/qa-framework-plan.md`](docs/qa-framework-plan.md)
- [`docs/performance-test-plan.md`](docs/performance-test-plan.md)
- [`docs/backend-endpoints-under-test.md`](docs/backend-endpoints-under-test.md)
- [`docs/performance-gates.md`](docs/performance-gates.md)

## Senior note

Do not run destructive tests, payment flows, WhatsApp sends, email sends, or heavy performance/security scans against production without sandbox integrations, seeded data, and an explicit run window. Professional QA protects the business while finding defects. 👑
