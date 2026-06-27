# Security Tests

OWASP ZAP baseline security scanning for King Sparkon Tracker.

## Run baseline scan

```bash
mvn -pl security-tests -Pzap-baseline verify \
  -Dsecurity.targetUrl=http://localhost:3000 \
  -Dzap.minutes=1
```

The ZAP baseline scan is passive by default and is suitable for CI smoke security checks. Do not confuse it with a full active penetration test.

## Reports

Reports are generated under:

```text
security-tests/target/zap
```

## What this checks first

- Missing security headers.
- Cookie flags.
- Sensitive information disclosure.
- Passive XSS-related warnings.
- Cache-control weaknesses.

## What comes later

- Authenticated scan context.
- API OpenAPI-driven scan.
- Active scan in a controlled non-production environment.
- Dependency vulnerability scanning.
