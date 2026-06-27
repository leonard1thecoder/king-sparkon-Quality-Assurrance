# King Sparkon Quality Assurance

A professional QA automation workspace for **King Sparkon Tracker**.

This repository is structured as a **Maven multi-module QA application** so backend API, web UI, Android, security, and performance test suites can live together without becoming a messy scripts folder.

## Current modules

| Module | Purpose |
| --- | --- |
| `backend-api-tests` | REST Assured API regression tests for the King Sparkon Tracker backend. |
| `selenium-web-e2e-tests` | Selenium WebDriver end-to-end tests for the Next.js website. |
| `security-tests` | OWASP ZAP baseline security scanning for the web/API surface. |
| `appium-android-tests` | Appium Android automation skeleton for future barcode scanner Android development. |
| `jmeter-performance-tests` | Apache JMeter performance tests for backend health, API load, and barcode scan spike scenarios. |

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
mvn -pl backend-api-tests test \
  -Dapi.baseUrl=http://localhost:8080 \
  -Dapi.authToken="YOUR_ACCESS_TOKEN"
```

Auth-required scenarios are skipped when `api.authToken` is empty.

## Run Selenium website E2E tests

```bash
mvn -pl selenium-web-e2e-tests test \
  -Dui.baseUrl=http://localhost:3000 \
  -Dui.browser=chrome \
  -Dui.headless=true
```

For Selenium Grid:

```bash
mvn -pl selenium-web-e2e-tests test \
  -Dui.baseUrl=http://localhost:3000 \
  -Dselenium.remoteUrl=http://localhost:4444/wd/hub
```

## Run Appium Android tests

Start Appium first, then run:

```bash
mvn -pl appium-android-tests test \
  -Dappium.serverUrl=http://127.0.0.1:4723 \
  -Dappium.deviceName="Android Emulator" \
  -Dappium.platformVersion=15 \
  -Dappium.appPackage=com.kingsparkon.tracker \
  -Dappium.appActivity=.MainActivity
```

Or pass an APK path:

```bash
mvn -pl appium-android-tests test \
  -Dappium.app=/absolute/path/to/king-sparkon-tracker.apk
```

## Run security baseline scan

```bash
mvn -pl security-tests -Pzap-baseline verify \
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

## Test strategy docs

- [`docs/qa-framework-plan.md`](docs/qa-framework-plan.md)
- [`docs/performance-test-plan.md`](docs/performance-test-plan.md)
- [`docs/backend-endpoints-under-test.md`](docs/backend-endpoints-under-test.md)
- [`docs/performance-gates.md`](docs/performance-gates.md)

## Senior note

Do not run destructive tests, payment flows, WhatsApp sends, email sends, or heavy performance/security scans against production without sandbox integrations, seeded data, and an explicit run window. Professional QA protects the business while finding defects. 👑
