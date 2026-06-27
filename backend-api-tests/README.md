# Backend API Tests

REST Assured regression tests for the King Sparkon Tracker Spring Boot backend.

## Run

```bash
mvn -pl backend-api-tests test \
  -Dapi.baseUrl=http://localhost:8080 \
  -Dapi.authToken="YOUR_ACCESS_TOKEN"
```

Auth-required scenarios are skipped when `api.authToken` is not supplied.

## Scenario file

```text
src/test/resources/scenarios/backend-api-test-cases.svc
```

Format:

```text
id|name|method|path|body|expectedStatus|requiresAuth
```

Use this module for API contract/regression checks. Keep performance testing in JMeter. Keep browser behavior in Selenium. 🚀
