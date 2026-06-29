# King Sparkon Quality Assurance

A professional QA automation workspace for **King Sparkon Tracker**.

This repository is structured as a **Maven multi-module MVC QA application** so backend API, web UI, product, Android, security, and performance test suites can live together without becoming a messy scripts folder.

## Last-polish readiness audit

This QA repo was reviewed against:

- Frontend: `leonard1thecoder/king-sparkon-tracker`
- Backend: `leonard1thecoder/king-sparkon-tracker-backend`
- QA: `leonard1thecoder/king-sparkon-Quality-Assurrance`

Important audit notes before real execution:

| Area | Current finding | QA action |
| --- | --- | --- |
| Frontend scripts | Frontend has `lint`, `typecheck`, `test`, and `build` scripts using Bun/Vitest. | QA smoke/E2E can call the deployed/local frontend, but frontend unit coverage still needs real `.test.ts/.test.tsx` files if missing. |
| Frontend CI | Frontend CI runs `bun install`, `lint`, `typecheck`, `test`, and `build`. | QA should still flag frozen-lockfile enforcement as a frontend CI polish item. |
| Frontend contract | Frontend README documents `/api/products`, `/api/transactions`, `/api/tips`, reports, audit, billing, affiliate, and auth proxy flows. | QA REST/JMeter contracts now use `/api/...`, not `/api/v1/...`. |
| Backend build | Backend Docker build runs `scripts/full-maven-scan.sh`, which executes `mvn -B clean verify`. | Backend build gate is strong enough for pre-release validation. |
| Backend test scope | Backend README requires auth, authorization, tenant isolation, stock, barcode, Stripe, PayPal, tips, subscribers, promotions, cache, rate limit, session, and config gates. | QA repo maps these into automated, manual, and planned test gates. |
| Claims and returnables | Backend/frontend READMEs do not yet clearly document final claims/returnables controllers. | QA keeps claims/returnables as contract-target scenarios, but manual confirmation is required before runtime REST gate is made mandatory. |
| Code search | GitHub reports the app repos are not code-search indexed. | This audit is based on README, package/pom, CI, Docker, and known contract files rather than full repository tree search. |

## Current modules

| Module | Purpose |
| --- | --- |
| `qa-core-framework` | Shared MVC test models, test-builder view contract, locator framework, screenshot upload, response objects, E2E steps, automation-analysis models, and HTML reporting. |
| `backend-api-tests` | REST Assured API regression, smoke, and business-flow definition tests for the King Sparkon Tracker backend. |
| `selenium-web-e2e-tests` | Selenium WebDriver end-to-end tests and screenshot capture for the Next.js website. |
| `security-tests` | OWASP ZAP baseline security scanning plus MVC readiness report. |
| `appium-android-tests` | Appium Android automation and screenshot capture for future barcode scanner Android development. |
| `jmeter-performance-tests` | Apache JMeter performance tests for backend health, API load, barcode scan spike, web-page load, whole-project load, and REST business-flow load scenarios. |
| `testProduct` | MVC product test-definition module for catalogue, barcode lookup, inventory, pricing, and dashboard product QA readiness. |

## Automated coverage already defined

| Coverage area | Automated asset | Status before real environment |
| --- | --- | --- |
| Backend health/docs/API smoke | `backend-api-tests/src/test/resources/scenarios/backend-api-test-cases.svc` | Ready for runtime when backend is running. |
| REST business flows | `backend-api-tests/src/test/resources/scenarios/rest/*-rest-test-cases.svc` | Definition-ready; runtime requires seeded data and auth token. |
| REST business smoke | `backend-api-tests/src/test/resources/scenarios/rest/rest-business-smoke-test-cases.svc` | Ready as a fast smoke gate. |
| REST business steps | `backend-api-tests/src/test/resources/scenarios/rest/rest-business-flow-test-steps.scv` | Step-backed for every business-flow scenario. |
| Product definitions | `testProduct/src/test/resources/scenarios/product-test-cases.svc` and `product-test-steps.scv` | Ready as definition/report gate. |
| Web UI scenarios | `selenium-web-e2e-tests/src/test/resources/scenarios/web-ui-test-cases.svc` | Ready when frontend URL is reachable. |
| Screenshot locators | `selenium-web-e2e-tests/src/test/resources/scenarios/web-screenshot-locators.svc` | Ready when stable selectors exist in UI. |
| Android placeholders | `appium-android-tests/src/test/resources/data/android-app-data.svc` | Placeholder-ready; needs built Android app or emulator config. |
| Security baseline | `security-tests` ZAP profile | Manual dispatch only; do not run against production without approval. |
| JMeter performance | `jmeter-performance-tests/src/test/jmeter/*.jmx` | Ready for controlled environments. |
| REST business performance | `king-sparkon-rest-business-flow-load.jmx` + `rest-business-flow-endpoints.csv` | Ready for sandbox/staging with seeded data. |

## Manual test plan before real automation execution

Some flows must be manually verified once before they are trusted as hard CI gates. These manual checks produce business evidence that automation cannot fully prove alone.

### Frontend manual tests

| ID | Area | Manual check | Evidence to capture |
| --- | --- | --- | --- |
| MANUAL-FE-001 | Landing page | Confirm white professional UI, 3D hero/model stays, scanner orange line does not drop below barcode area, and no AI-slop visuals. | Screenshot desktop + mobile. |
| MANUAL-FE-002 | Auth pages | Confirm login/register are separate, linked, not too square, placeholders explain every required field, and SEO metadata is present. | Screenshot + HTML title/description. |
| MANUAL-FE-003 | Footer/socials | Confirm professional footer has Facebook, Instagram, X, LinkedIn, and GitHub profile links. | Screenshot + link click evidence. |
| MANUAL-FE-004 | Role dashboards | Confirm admin/owner/worker/affiliate nav and layout match role capabilities. | Screenshot each dashboard. |
| MANUAL-FE-005 | Scanner UX | Confirm camera permission, barcode manual input fallback, success state, invalid state, loading state, and mobile viewport. | Screenshot + short screen recording. |
| MANUAL-FE-006 | Error UX | Confirm `401`, `403 EMAIL_NOT_VERIFIED`, `429`, validation, and network errors show readable messages. | Screenshot per state. |

### Backend manual tests

| ID | Area | Manual check | Evidence to capture |
| --- | --- | --- | --- |
| MANUAL-BE-001 | Seed data | Create owner, business, worker, product, barcode, subscriber, and transaction seed records. | Seed file or SQL notes. |
| MANUAL-BE-002 | Auth/session | Verify register, login, refresh, logout, forgot/reset password, and email verification. | API responses + logs. |
| MANUAL-BE-003 | Authorization | Confirm worker/affiliate cannot access owner/admin-only endpoints. | 403 evidence. |
| MANUAL-BE-004 | Tenant isolation | Confirm users from one business cannot read or mutate another business's products, tips, transactions, reports, or claims. | API responses. |
| MANUAL-BE-005 | Stock/barcodes | Confirm duplicate barcode rejection, sold barcode cannot be reused, and concurrent SELL cannot oversell stock. | API responses + DB state. |
| MANUAL-BE-006 | Stripe sandbox | Confirm website payment link creation, webhook signature failure, duplicate webhook idempotency, and successful payment state transition. | Stripe test event evidence. |
| MANUAL-BE-007 | PayPal sandbox | Confirm billing and payout/webhook behavior in sandbox only. | PayPal sandbox evidence. |
| MANUAL-BE-008 | WhatsApp/email sandbox | Confirm Twilio WhatsApp and SMTP messages are sent or safely skipped by config. | Provider logs or app logs. |
| MANUAL-BE-009 | Redis/rate limit | Confirm Redis profile and memory fallback; confirm `429` body and headers. | API responses + logs. |
| MANUAL-BE-010 | Observability | Confirm Actuator health, Prometheus metrics, and Grafana dashboard import. | Screenshot or endpoint output. |

### Business-flow manual tests

| ID | Area | Manual check | Evidence to capture |
| --- | --- | --- | --- |
| MANUAL-BIZ-001 | Tips | Worker creates tip, QR/payment link works, invalid amount rejected, owner marks paid, duplicate paid transition rejected. | UI screenshots + API responses. |
| MANUAL-BIZ-002 | Tip withdrawals | Confirm configured fee only, hold period, minimum amount, owner-only withdrawal, and dashboard totals. | API response + DB/ledger values. |
| MANUAL-BIZ-003 | Transactions | Confirm BUY/SELL rules, website payment contact capture, CASH/SWIPE no auto-subscribe, and withdrawal request. | API response + UI detail view. |
| MANUAL-BIZ-004 | Claims | Confirm final controller path, create/approve/reject lifecycle, missing reason validation, duplicate reference conflict. | API response + controller mapping. |
| MANUAL-BIZ-005 | Returnables | Confirm final controller path, create/list/mark returned/refund preview lifecycle, bad quantity validation, duplicate returned conflict. | API response + controller mapping. |
| MANUAL-BIZ-006 | Reports/audit | Confirm inventory summary, alcohol report, product movement, and audit logs update after transactions/tips. | Report output + audit records. |

## Known gaps before hard CI gating

| Gap | Why it matters | Action |
| --- | --- | --- |
| Frontend unit tests need confirmation | `bun run test` exists, but app-level test files still need confirmation once repo code search/index is available. | Add Vitest tests for money/date/error helpers, API error normalization, auth guards, and scanner fallback states. |
| Claims/returnables backend mapping needs confirmation | Current frontend/backend README does not clearly expose final controller paths. | Confirm or implement `/api/claims` and `/api/returnables`, then make runtime tests mandatory. |
| Runtime REST requires seeded IDs | Scenario IDs such as worker `1001`, tip `9001`, claim `7001`, returnable `8001`, transaction `3001` must exist. | Add a dedicated QA seed script or Testcontainers seed fixture. |
| Payment/provider tests need sandbox only | Stripe/PayPal/Twilio/SMTP calls must not hit production during QA. | Add sandbox secrets and explicit run window. |
| Frontend CI install command mismatch | Frontend README references frozen install, workflow currently uses plain `bun install`. | Update frontend CI to `bun install --frozen-lockfile` after lockfile is stable. |
| Heavy JMeter and ZAP should not run on every PR | They can create noise, cost, and false failures without a live target. | Keep manual dispatch; run on staging or scheduled release windows. |

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
| View | `view` | Service-side logic that builds and executes test cases. View classes implement `TestBuilder` and build reportable responses. |
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

Never commit Supabase keys. Use GitHub Actions secrets or local environment variables.

## HTML reports

Java controller tests write reports under each module:

```text
target/qa-report/*.html
```

The report contains total duration, test counts, pass/fail percentage, pie chart, and a result table with case ID, scenario, test case name, description, status, duration, and error message.

## Default apps under test

| App | Default URL |
| --- | --- |
| Backend API | `http://localhost:8080` |
| Website frontend | `http://localhost:3000` |
| Appium server | `http://127.0.0.1:4723` |

## Scenario and app data files

| File | Purpose |
| --- | --- |
| `test-assets/scenarios/king-sparkon-test-cases.svc` | Central cross-platform test case inventory. |
| `test-assets/test-cases/king-sparkon-test-cases.csv` | CSV test inventory across smoke, API, web, product, tips, security, performance, and Android. |
| `test-assets/test-cases/rest-business-flow-test-cases.csv` | REST business-flow CSV inventory for tips, claims, returnables, and transactions. |
| `test-assets/scenarios/king-sparkon-test-steps.scv` | Cross-project step catalogue. |
| `backend-api-tests/src/test/resources/scenarios/backend-api-test-cases.svc` | API endpoint scenarios. |
| `backend-api-tests/src/test/resources/scenarios/rest/rest-business-smoke-test-cases.svc` | Fast REST business smoke scenarios. |
| `backend-api-tests/src/test/resources/scenarios/rest/rest-business-flow-test-steps.scv` | Step definitions for REST business scenarios. |
| `selenium-web-e2e-tests/src/test/resources/scenarios/web-ui-test-cases.svc` | Website page and UI scenarios. |
| `appium-android-tests/src/test/resources/data/android-app-data.svc` | Android/Appium app identifiers and locator placeholders. |
| `jmeter-performance-tests/src/test/resources/data/rest-business-flow-endpoints.csv` | REST business-flow JMeter endpoint matrix. |

## Run backend API and REST business tests

Definition/report mode:

```bash
mvn -am -pl backend-api-tests test \
  -Dapi.baseUrl=http://localhost:8080
```

Runtime REST mode:

```bash
mvn -am -pl backend-api-tests test \
  -Dapi.executeRestFlows=true \
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

## Run security baseline scan

```bash
mvn -am -pl security-tests -Pzap-baseline verify \
  -Dsecurity.targetUrl=http://localhost:3000
```

## Run performance tests locally

```bash
mvn -am -pl jmeter-performance-tests verify \
  -DtargetProtocol=http \
  -DtargetHost=localhost \
  -DtargetPort=8080 \
  -Dthreads=10 \
  -DrampUpSeconds=30 \
  -DdurationSeconds=60 \
  -DauthToken="YOUR_ACCESS_TOKEN"
```

## GitHub Actions

| Workflow | Purpose |
| --- | --- |
| `QA Framework Build` | Compiles Java QA modules on PRs. |
| `QA API and Web Smoke` | Manual backend API + Selenium website smoke/screenshot run and uploads MVC HTML reports/screenshots. |
| `REST Business Flow QA` | PR/manual REST business definition and smoke gate for tips, claims, returnables, and transactions. |
| `REST Business Performance QA` | Manual JMeter REST business-flow performance run. |
| `Security ZAP Baseline` | Manual OWASP ZAP passive baseline scan and uploads MVC/ZAP reports. |
| `JMeter Performance Tests` | Manual backend/web/barcode/whole-project performance run. |

## Test strategy docs

- [`docs/releases/king-sparkon-release-roadmap.md`](docs/releases/king-sparkon-release-roadmap.md)
- [`docs/templates/automation-analysis-template.md`](docs/templates/automation-analysis-template.md)
- [`docs/screenshot-locator-supabase.md`](docs/screenshot-locator-supabase.md)
- [`docs/qa-framework-plan.md`](docs/qa-framework-plan.md)
- [`docs/performance-test-plan.md`](docs/performance-test-plan.md)
- [`docs/backend-endpoints-under-test.md`](docs/backend-endpoints-under-test.md)
- [`docs/performance-gates.md`](docs/performance-gates.md)
- [`QA_AUTOMATION_PLAN.md`](QA_AUTOMATION_PLAN.md)

## Senior note

Do not run destructive tests, payment flows, WhatsApp sends, email sends, or heavy performance/security scans against production without sandbox integrations, seeded data, and an explicit run window. Professional QA protects the business while finding defects. 👑
