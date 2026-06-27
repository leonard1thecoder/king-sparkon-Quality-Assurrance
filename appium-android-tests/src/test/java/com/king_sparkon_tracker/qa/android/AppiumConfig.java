package com.king_sparkon_tracker.qa.android;

public record AppiumConfig(
        String serverUrl,
        String platformName,
        String platformVersion,
        String deviceName,
        String automationName,
        String app,
        String appPackage,
        String appActivity
) {

    public static AppiumConfig fromSystemProperties() {
        return new AppiumConfig(
                System.getProperty("appium.serverUrl", "http://127.0.0.1:4723"),
                System.getProperty("appium.platformName", "Android"),
                System.getProperty("appium.platformVersion", ""),
                System.getProperty("appium.deviceName", "Android Emulator"),
                System.getProperty("appium.automationName", "UiAutomator2"),
                System.getProperty("appium.app", ""),
                System.getProperty("appium.appPackage", ""),
                System.getProperty("appium.appActivity", "")
        );
    }

    public boolean hasLaunchTarget() {
        return !app.isBlank() || (!appPackage.isBlank() && !appActivity.isBlank());
    }
}
