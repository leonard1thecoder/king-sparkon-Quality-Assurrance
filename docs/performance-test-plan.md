# King Sparkon Tracker Performance Test Plan

## Goal

Validate that the King Sparkon Tracker backend can support barcode scanning, dashboard reads, product/inventory reads, and public operational endpoints under realistic load before release.

## Backend target

Default local target:

```text
http://localhost:8080
```

Runtime properties can point the same suite to QA, staging, or a controlled production-like environment.

## Entry criteria

- Backend is deployed and reachable from the test runner.
- Database has seeded users, products, inventory, workers, and barcode records.
- For secured endpoints, a valid bearer token is available.
- Payment, WhatsApp, email, and external provider flows use sandbox credentials only.
- Rate limits are configured for the intended test profile.

## Scope

### In scope

1. **Health baseline**
   - `/actuator/health`
   - `/v3/api-docs`
   - `/swagger-ui.html`

2. **Core API load mix**
   - User profile/session endpoint.
   - Product list endpoint.
   - Inventory endpoint.
   - Dashboard/report endpoint.
   - Barcode scan endpoint.

3. **Barcode scan spike**
   - Repeated barcode scan payloads.
   - Short ramp-up and controlled virtual users.
   - Response-time and error monitoring.

### Out of scope for the first suite

- Real-money Stripe, PayPal, Ozow, or Yoco payments.
- Live WhatsApp/email delivery.
- Destructive owner/admin workflows unless seeded test data is isolated.
- Full browser UX performance. That belongs in a separate frontend Lighthouse/Playwright module.

## Workload model

| Test type | Threads | Ramp-up | Duration | Purpose |
| --- | ---: | ---: | ---: | --- |
| Smoke | 1-3 | 5s | 30s | Prove environment is healthy. |
| Baseline | 10 | 30s | 5m | Establish normal latency. |
| Load | 50 | 2m | 15m | Validate expected traffic. |
| Stress | 100+ | 5m | 20m | Find breaking point. |
| Spike | 50-200 | 10-30s | 5m | Validate barcode scan burst behavior. |
| Soak | 25-50 | 5m | 1-4h | Catch leaks, connection-pool problems, and slow degradation. |

## Test data

Use stable test records. Do not use real customer, worker, owner, or payment data. For barcode scans, use repeatable EAN-like values in `barcode-scan-payloads.csv`.

## Pass/fail gates

See [`performance-gates.md`](performance-gates.md).

## Exit criteria

- Error rate is below the agreed gate.
- p95 response time is below the agreed endpoint budget.
- No uncontrolled JVM memory growth during soak tests.
- Database CPU, connection pool usage, and slow queries are understood.
- Logs contain request IDs for slow or failed requests.

## Reporting

After each run, capture:

- Environment URL.
- Git commit SHA for backend.
- Test profile: threads, ramp-up, duration.
- JMeter `.jtl` results and generated reports.
- Backend logs for slow or failed requests.
- Database/Redis/CPU/memory charts where available.

## Mentor note

Performance testing is not pressing “run” with 1000 users. Start small, protect your data, prove the scenario is correct, then increase pressure. Bad test data creates fake performance conclusions. 👑
