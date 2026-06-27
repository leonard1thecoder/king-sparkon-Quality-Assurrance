# Appium Android Tests

Appium Android automation skeleton for King Sparkon Tracker Android development.

## Prerequisites

- Android Studio / Android SDK.
- Android emulator or physical device.
- Appium server running locally.
- UiAutomator2 driver installed.

```bash
appium driver install uiautomator2
appium
```

## Run with installed app package

```bash
mvn -pl appium-android-tests test \
  -Dappium.serverUrl=http://127.0.0.1:4723 \
  -Dappium.deviceName="Android Emulator" \
  -Dappium.platformVersion=15 \
  -Dappium.appPackage=com.kingsparkon.tracker \
  -Dappium.appActivity=.MainActivity
```

## Run with APK path

```bash
mvn -pl appium-android-tests test \
  -Dappium.serverUrl=http://127.0.0.1:4723 \
  -Dappium.deviceName="Android Emulator" \
  -Dappium.platformVersion=15 \
  -Dappium.app=/absolute/path/to/king-sparkon-tracker.apk
```

## Data file

```text
src/test/resources/data/android-app-data.svc
```

Use accessibility IDs in the Android app for stable tests. Do not make Appium tests depend on fragile text-only selectors.
