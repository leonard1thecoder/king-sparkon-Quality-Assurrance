package com.king_sparkon_tracker.qa.android;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class BarcodeScannerAndroidTest {

    private AndroidDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void barcodeScannerControlsShouldBeUsableWhenAccessibilityIdsExist() {
        AppiumConfig config = AppiumConfig.fromSystemProperties();
        Assumptions.assumeTrue(config.hasLaunchTarget(), "Skipping scanner test. Provide appium.app or appium.appPackage + appium.appActivity.");

        String barcodeInputId = System.getProperty("appium.barcodeInputAccessibilityId", "barcode-input");
        String scanButtonId = System.getProperty("appium.scanButtonAccessibilityId", "scan-button");
        String sampleBarcode = System.getProperty("appium.sampleBarcode", "6009801234567");

        driver = AndroidDriverFactory.create(config);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement barcodeInput = wait.until(ExpectedConditions.presenceOfElementLocated(AppiumBy.accessibilityId(barcodeInputId)));
        barcodeInput.sendKeys(sampleBarcode);

        WebElement scanButton = wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.accessibilityId(scanButtonId)));
        scanButton.click();

        assertThat(driver.getSessionId()).as("Scanner test should keep Appium session alive after scan click").isNotNull();
    }
}
