# Backend API Tests

REST Assured regression tests for the King Sparkon Tracker Spring Boot backend.

The default target is the current pre-prod Cloud Run backend:

```text
https://king-sparkon-tracker-backend-75756350962.africa-south1.run.app
```

## Run pre-prod API coverage

```bash
mvn -am -pl backend-api-tests test
```

Override the backend or pass an auth token when you want positive protected-path checks:

```bash
mvn -am -pl backend-api-tests test \
  -Dapi.baseUrl=https://king-sparkon-tracker-backend-75756350962.africa-south1.run.app \
  -Dapi.authToken="YOUR_ACCESS_TOKEN"
```

Auth-required positive scenarios are skipped when `api.authToken` is not supplied. Public smoke and unauthenticated auth-wall scenarios still run, so pre-prod gets useful coverage without secrets.

## Scenario file

```text
src/test/resources/scenarios/backend-api-test-cases.svc
```

Format:

```text
id|name|method|path|body|expectedStatuses|requiresAuth
```

`expectedStatuses` supports one status or a comma-separated set, for example `200,302` for Swagger redirect behavior or `401,403` for Spring Security auth-wall behavior.

## Current coverage gates

The controller/unit coverage tests enforce that the scenario inventory covers:

- health, OpenAPI, and Swagger readiness;
- user dashboard, business cards, workers, events, and tip links;
- public ticket events, ticket purchase, my tickets, and boosts;
- business account summary, ledger, top-up, and confirmation auth walls;
- positive protected placeholders for users, products, inventory, reports, and barcode scans when `api.authToken` is supplied.

Keep payment, WhatsApp, email, Stripe, and top-up write flows auth-walled by default. Do not run money-movement paths against production/pre-prod as successful write scenarios unless sandbox credentials, seeded data, and an explicit run window are configured. 🚀
