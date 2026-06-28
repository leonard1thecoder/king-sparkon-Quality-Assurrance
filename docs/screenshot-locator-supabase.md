# Screenshot, Locator, and Supabase Upload Framework

## Purpose

The QA framework can now capture Selenium and Appium screenshots, save them locally, and optionally upload them to Supabase Storage.

## Supported locator strategies

| Strategy | Selenium | Appium |
| --- | --- | --- |
| `id` | Yes | Yes |
| `name` | Yes | Yes |
| `css` | Yes | Driver dependent |
| `class-name` | Yes | Yes |
| `tag-name` | Yes | Yes |
| `link-text` | Yes | Driver dependent |
| `partial-link-text` | Yes | Driver dependent |
| `xpath` | Yes | Yes |
| `full-xpath` | Yes | Yes |
| `js-path` | Yes | No for native Android |
| `accessibility-id` | No | Yes |
| `android-ui-automator` | No | Yes |
| `ios-predicate` | No | Future iOS support |
| `ios-class-chain` | No | Future iOS support |

## Locator file format

```text
id|pagePath|strategy|locatorValue|expectedText|description
```

Selenium file:

```text
selenium-web-e2e-tests/src/test/resources/scenarios/web-screenshot-locators.svc
```

Appium file:

```text
appium-android-tests/src/test/resources/scenarios/android-screenshot-locators.svc
```

## Selenium examples

```text
WEB-SCREEN-HOME-HERO|/|xpath|//body|King Sparkon|Capture landing page body through XPath
WEB-SCREEN-REGISTER-FORM|/register|full-xpath|/html/body||Capture register page using full XPath example
WEB-SCREEN-HOME-JS|/|js-path|return document.body|King Sparkon|Capture home page using JavaScript path
```

## Appium examples

```text
ANDROID-SCREEN-BARCODE-INPUT|/|accessibility-id|barcode-input||Capture barcode input by accessibility id
ANDROID-SCREEN-SCAN-BUTTON|/|android-ui-automator|new UiSelector().description("scan-button")||Capture scan button using Android UiAutomator
```

## Local screenshot output

Screenshots are always written locally first:

```text
target/qa-screenshots/*.png
```

## Supabase upload

Supabase upload is disabled by default. Enable it with Maven properties:

```bash
mvn -am -pl selenium-web-e2e-tests test \
  -Dsupabase.uploadEnabled=true \
  -Dsupabase.url=https://YOUR_PROJECT.supabase.co \
  -Dsupabase.bucket=qa-screenshots \
  -Dsupabase.serviceRoleKey=YOUR_SERVICE_ROLE_KEY
```

Or with environment variables:

```text
SUPABASE_UPLOAD_ENABLED=true
SUPABASE_URL=https://YOUR_PROJECT.supabase.co
SUPABASE_BUCKET=qa-screenshots
SUPABASE_SERVICE_ROLE_KEY=YOUR_SERVICE_ROLE_KEY
```

Uploaded screenshots use this remote path format:

```text
qa-screenshots/{testCaseId}/{fileName}.png
```

## Security note

Never commit Supabase service role keys. Store them in GitHub Actions secrets or local environment variables.
