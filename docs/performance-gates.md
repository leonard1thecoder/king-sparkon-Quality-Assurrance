# Performance Gates

These are starter gates for King Sparkon Tracker. Tune them with real baseline data after the first stable runs.

## Global gates

| Metric | Gate |
| --- | ---: |
| Error rate | `< 1%` for load tests |
| Health endpoint p95 | `< 300ms` |
| Read API p95 | `< 800ms` |
| Barcode scan p95 | `< 1000ms` |
| Barcode scan p99 | `< 2000ms` |
| Backend CPU | `< 75%` sustained during expected load |
| Database connection pool | No sustained exhaustion |

## Smoke profile

Smoke runs prove reachability only. They should fail fast on:

- Backend unavailable.
- Health endpoint not returning `200`.
- Swagger/OpenAPI endpoint broken when docs are expected to be exposed.

## Load profile

Load runs should represent expected business traffic:

- Normal user reads.
- Worker barcode scans.
- Owner dashboard reads.
- No real external money movement.

## Stress profile

Stress runs intentionally exceed expected traffic to find the breaking point. Do not use stress results as normal release gates unless the environment matches production capacity.

## Spike profile

Spike runs are important for barcode scanning because many workers can scan during the same operational window. Watch:

- Lock contention.
- Database write latency.
- Duplicate scan handling.
- Rate-limit behavior.
- Request IDs in failed responses.

## Release decision

A release is blocked when:

- Error rate exceeds the gate.
- p95 or p99 breaks the agreed budget on a critical path.
- Backend logs show repeated DB connection exhaustion.
- External provider calls are accidentally hit outside sandbox.
- Test data is invalid and results cannot be trusted.
