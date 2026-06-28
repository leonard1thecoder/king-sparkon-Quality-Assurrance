# Backend Endpoints Under Test

The first JMeter module targets the King Sparkon Tracker backend and keeps paths configurable through CSV files.

## Public and operational endpoints

| Endpoint | Method | Purpose |
| --- | --- | --- |
| `/actuator/health` | `GET` | Backend health check. |
| `/v3/api-docs` | `GET` | OpenAPI JSON. |
| `/swagger-ui.html` | `GET` | Swagger UI redirect/page. |

## Application endpoints in first load matrix

These are starter paths for the backend load matrix and should be aligned with controller paths as the backend stabilizes.

| Endpoint | Method | Purpose |
| --- | --- | --- |
| `/api/v1/users/me` | `GET` | Current user/session profile. |
| `/api/v1/products` | `GET` | Product catalogue/listing. |
| `/api/v1/inventory` | `GET` | Inventory state. |
| `/api/v1/reports/dashboard` | `GET` | Dashboard/report summary. |
| `/api/v1/barcodes/scan` | `POST` | Barcode scan flow. |

## How to update the matrix

Edit:

```text
jmeter-performance-tests/src/test/resources/data/api-endpoints.csv
```

Format:

```text
name|method|path|body|expectedStatus
```

Examples:

```text
products|GET|/api/v1/products||200
barcode-scan|POST|/api/v1/barcodes/scan|{"barcode":"6009801234567","deviceId":"jmeter-device-001"}|200
```

## Authentication

Protected endpoints use the `authToken` Maven/JMeter property:

```bash
-DauthToken="YOUR_ACCESS_TOKEN"
```

The JMeter plans send it as an HTTP Authorization header with bearer authentication.

## Payment and messaging endpoints

Do not add real payment, WhatsApp, or email flows to the default load matrix. Add sandbox-only plans with idempotency keys and low volume when those integrations are ready.
