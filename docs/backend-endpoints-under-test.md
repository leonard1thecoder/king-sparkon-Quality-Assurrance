# Backend Endpoints Under Test

The first REST Assured and JMeter modules target the King Sparkon Tracker backend and keep paths configurable through SVC/CSV files.

## Current pre-prod backend

```text
https://king-sparkon-tracker-backend-75756350962.africa-south1.run.app
```

## Public and operational endpoints

| Endpoint | Method | Purpose | Expected status |
| --- | --- | --- | --- |
| `/actuator/health` | `GET` | Backend health check. | `200` |
| `/v3/api-docs` | `GET` | OpenAPI JSON. | `200` |
| `/swagger-ui.html` | `GET` | Swagger UI redirect/page. | `200,302` |
| `/api/v1/tickets/events` | `GET` | Public/upcoming ticket events. | `200,204` |

## Protected endpoint auth-wall matrix

These scenarios run without secrets and prove that sensitive endpoints are not open in pre-prod.

| Endpoint | Method | Purpose | Expected status |
| --- | --- | --- | --- |
| `/api/user-dashboard` | `GET` | User dashboard feed is protected. | `401,403` |
| `/api/user-dashboard/businesses` | `GET` | Business cards are protected. | `401,403` |
| `/api/user-dashboard/businesses/{businessId}/workers` | `GET` | Worker tip cards are protected. | `401,403` |
| `/api/user-dashboard/businesses/{businessId}/events` | `GET` | Business event list is protected. | `401,403` |
| `/api/user-dashboard/tips` | `POST` | Worker tip Stripe payment link creation is protected. | `401,403` |
| `/api/v1/tickets/me/purchase` | `POST` | Ticket checkout link creation is protected. | `401,403` |
| `/api/v1/tickets/me/tickets` | `GET` | Purchased ticket list is protected. | `401,403` |
| `/api/v1/tickets/me/events/{eventId}/boosts` | `POST` | Owner ticket event boost is protected. | `401,403` |
| `/api/v1/tickets/me/event-boosts` | `GET` | Owner ticket boost list is protected. | `401,403` |
| `/api/business-account/summary` | `GET` | Owner account balance is protected. | `401,403` |
| `/api/business-account/ledger` | `GET` | Owner account ledger is protected. | `401,403` |
| `/api/business-account/top-ups` | `POST` | Business account top-up link creation is protected. | `401,403` |
| `/api/business-account/top-ups/{entryId}/confirm` | `POST` | Business account top-up confirmation is protected. | `401,403` |

## Auth-token positive matrix

These scenarios are skipped unless `-Dapi.authToken="YOUR_ACCESS_TOKEN"` is supplied.

| Endpoint | Method | Purpose |
| --- | --- | --- |
| `/api/v1/users/me` | `GET` | Current user/session profile. |
| `/api/v1/products` | `GET` | Product catalogue/listing. |
| `/api/v1/inventory` | `GET` | Inventory state. |
| `/api/v1/reports/dashboard` | `GET` | Dashboard/report summary. |
| `/api/v1/barcodes/scan` | `POST` | Barcode scan flow. |

## How to update the REST Assured matrix

Edit:

```text
backend-api-tests/src/test/resources/scenarios/backend-api-test-cases.svc
```

Format:

```text
id|name|method|path|body|expectedStatuses|requiresAuth
```

Examples:

```text
API-HEALTH-001|Health check|GET|/actuator/health||200|false
API-SWAGGER-001|Swagger UI|GET|/swagger-ui.html||200,302|false
USER-DASHBOARD-AUTH-001|User dashboard requires authentication|GET|/api/user-dashboard||401,403|false
```

## JMeter load matrix

Edit:

```text
jmeter-performance-tests/src/test/resources/data/api-endpoints.csv
```

Format:

```text
name|method|path|body|expectedStatus
```

Keep heavy load, payment, WhatsApp, email, and Stripe workflows out of the default pre-prod run unless sandbox integrations, seeded data, and an explicit run window exist.

## Authentication

Protected positive endpoints use the `api.authToken` Maven property in REST Assured and the `authToken` Maven/JMeter property in JMeter:

```bash
-Dapi.authToken="YOUR_ACCESS_TOKEN"
-DauthToken="YOUR_ACCESS_TOKEN"
```

The test plans send it as an HTTP Authorization bearer token.
