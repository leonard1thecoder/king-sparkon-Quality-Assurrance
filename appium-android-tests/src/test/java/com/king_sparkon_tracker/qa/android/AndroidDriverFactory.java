package com.king_sparkon_tracker.qa.android;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.URI;
import java.net.URL;
import java.time.Duration;

public final class AndroidDriverFactory {

    private AndroidDriverFactory() {
    }

    public static AndroidDriver create(AppiumConfig config) {
        try {
            UiAutomator2Options options = new UiAutomator2Options()
                    .setPlatformName(config.platformName())
                    .setAutomationName(config.automationName())
                    .setDeviceName(config.deviceName())
                    .setNewCommandTimeout(Duration.ofSeconds(90));

            if (!config.platformVersion().isBlank()) {
                options.setPlatformVersion(config.platformVersion());
            }
            if (!config.app().isBlank()) {
                options.setApp(config.app());
            }
            if (!config.appPackage().isBlank()) {
                options.setAppPackage(config.appPackage());
            }
            if (!config.appActivity().isBlank()) {
                options.setAppActivity(config.appActivity());
            }

            URL serverUrl = URI.create(config.serverUrl()).toURL();
            return new AndroidDriver(serverUrl, options);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to create AndroidDriver. Check Appium server, emulator, and app capabilities.", exception);
        }
    }
}
