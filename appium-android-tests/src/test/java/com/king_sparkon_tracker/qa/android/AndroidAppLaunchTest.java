package com.king_sparkon_tracker.qa.android;

import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AndroidAppLaunchTest {

    private AndroidDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void androidAppShouldLaunch() {
        AppiumConfig config = AppiumConfig.fromSystemProperties();
        Assumptions.assumeTrue(config.hasLaunchTarget(), "Skipping Android launch test. Provide appium.app or appium.appPackage + appium.appActivity.");

        driver = AndroidDriverFactory.create(config);

        assertThat(driver.getSessionId()).as("Appium session should start").isNotNull();
    }
}
