# Selenium Web E2E Tests

Selenium WebDriver tests for the King Sparkon Tracker website.

## Run locally

```bash
mvn -pl selenium-web-e2e-tests test \
  -Dui.baseUrl=http://localhost:3000 \
  -Dui.browser=chrome \
  -Dui.headless=true
```

## Run against Selenium Grid

```bash
mvn -pl selenium-web-e2e-tests test \
  -Dui.baseUrl=http://localhost:3000 \
  -Dselenium.remoteUrl=http://localhost:4444/wd/hub
```

## Scenario file

```text
src/test/resources/scenarios/web-ui-test-cases.svc
```

Format:

```text
id|name|path|expectedContent|requiresAuth
```

## UI locator standard

For stable tests, add `data-testid` attributes in the Next.js app. Example:

```tsx
<input data-testid="login-email" type="email" placeholder="Email address" />
```

Do not build serious tests around generated CSS class names. That is weak QA. 👑
