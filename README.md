# King Sparkon Quality Assurance

A professional QA automation workspace for **King Sparkon Tracker**.

This repository is structured as a **Maven multi-module QA application** so performance, API, security, and future non-functional test suites can live together without becoming a messy scripts folder.

## Current modules

| Module | Purpose |
| --- | --- |
| `jmeter-performance-tests` | Apache JMeter performance tests for the King Sparkon Tracker backend. Includes health, API load, and barcode scan scenarios. |

## Backend under test

Default target backend:

```text
http://localhost:8080
```

The backend target is configurable at runtime through Maven properties, so the same tests can run against local, QA, staging, or production-like environments.

## Run performance tests locally

From the repository root:

```bash
mvn -pl jmeter-performance-tests verify \
  -DtargetProtocol=http \
  -DtargetHost=localhost \
  -DtargetPort=8080 \
  -Dthreads=10 \
  -DrampUpSeconds=30 \
  -DdurationSeconds=60
```

For protected endpoints, pass a JWT or backend access token:

```bash
mvn -pl jmeter-performance-tests verify \
  -DtargetProtocol=http \
  -DtargetHost=localhost \
  -DtargetPort=8080 \
  -DauthToken="YOUR_ACCESS_TOKEN"
```

## Reports

JMeter result files are written under:

```text
jmeter-performance-tests/target/jmeter
```

CI uploads this directory as a GitHub Actions artifact after a manual performance run.

## Test strategy

See:

- [`docs/performance-test-plan.md`](docs/performance-test-plan.md)
- [`docs/backend-endpoints-under-test.md`](docs/backend-endpoints-under-test.md)
- [`docs/performance-gates.md`](docs/performance-gates.md)

## Senior note

Do not run performance tests against production payment endpoints without sandbox keys, rate-limit coordination, and seeded data. Load testing payment, email, WhatsApp, or Stripe/PayPal flows blindly is how teams create expensive incidents. Test those flows in sandbox first. 👑
