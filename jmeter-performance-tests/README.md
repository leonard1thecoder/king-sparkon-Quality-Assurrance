# JMeter Performance Tests

This module contains the first performance suite for the King Sparkon Tracker backend.

## Scenarios

| JMeter plan | Scenario | Main purpose |
| --- | --- | --- |
| `king-sparkon-backend-health.jmx` | Health and API documentation baseline | Proves the backend is reachable and checks low-cost public endpoints. |
| `king-sparkon-backend-api-load.jmx` | Configurable backend API load mix | Reads endpoint definitions from `api-endpoints.csv` so the suite evolves with the API. |
| `king-sparkon-barcode-scan-spike.jmx` | Barcode scan spike test | Focuses on the scanner use case with repeat barcode payloads. |

## Runtime properties

| Property | Default | Purpose |
| --- | --- | --- |
| `targetProtocol` | `http` | Backend protocol. |
| `targetHost` | `localhost` | Backend host. |
| `targetPort` | `8080` | Backend port. Leave blank for default HTTP/HTTPS port. |
| `threads` | `10` | Virtual users. |
| `rampUpSeconds` | `30` | Ramp-up period. |
| `durationSeconds` | `60` | Test duration. |
| `thinkTimeMs` | `250` | Delay between requests. |
| `maxResponseMs` | `1500` | Per-request duration assertion. |
| `authToken` | empty | Bearer token for protected endpoints. |
| `endpointCsv` | `src/test/resources/data/api-endpoints.csv` | Endpoint matrix for API load plan. |
| `barcodeCsv` | `src/test/resources/data/barcode-scan-payloads.csv` | Barcode payload data. |
| `barcodeScanPath` | `/api/v1/barcodes/scan` | Barcode scan API path. |
| `expectedBarcodeStatus` | `200` | Expected status for scan calls. |

## Local command

```bash
mvn -pl jmeter-performance-tests verify \
  -DtargetProtocol=http \
  -DtargetHost=localhost \
  -DtargetPort=8080 \
  -Dthreads=25 \
  -DrampUpSeconds=60 \
  -DdurationSeconds=300 \
  -DauthToken="YOUR_ACCESS_TOKEN"
```

## Tuning the endpoint matrix

Edit:

```text
src/test/resources/data/api-endpoints.csv
```

The format is pipe-delimited to allow JSON request bodies:

```text
name|method|path|body|expectedStatus
```

Keep write-heavy, payment, WhatsApp, and email flows out of high-volume load runs unless you are using a sandbox and idempotent test records. ⚠️
