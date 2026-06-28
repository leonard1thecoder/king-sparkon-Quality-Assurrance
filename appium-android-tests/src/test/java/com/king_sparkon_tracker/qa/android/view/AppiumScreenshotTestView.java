package com.king_sparkon_tracker.qa.android.view;

import com.king_sparkon_tracker.qa.android.AndroidDriverFactory;
import com.king_sparkon_tracker.qa.android.AppiumConfig;
import com.king_sparkon_tracker.qa.core.model.TestCaseModel;
import com.king_sparkon_tracker.qa.core.model.locator.ElementLocatorModel;
import com.king_sparkon_tracker.qa.core.model.screenshot.ScreenshotArtifact;
import com.king_sparkon_tracker.qa.core.model.screenshot.ScreenshotUploadResult;
import com.king_sparkon_tracker.qa.core.view.AbstractTestView;
import com.king_sparkon_tracker.qa.core.view.screenshot.ScreenshotFileWriter;
import com.king_sparkon_tracker.qa.core.view.storage.SupabaseScreenshotUploader;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class AppiumScreenshotTestView extends AbstractTestView {

    private final ElementLocatorModel locator;
    private final AppiumConfig config;
    private final AppiumLocatorResolver locatorResolver;
    private final ScreenshotFileWriter screenshotFileWriter;
    private final SupabaseScreenshotUploader uploader;

    public AppiumScreenshotTestView(ElementLocatorModel locator, AppiumConfig config) {
        this(locator, config, new AppiumLocatorResolver(), new ScreenshotFileWriter(), new SupabaseScreenshotUploader());
    }

    public AppiumScreenshotTestView(
            ElementLocatorModel locator,
            AppiumConfig config,
            AppiumLocatorResolver locatorResolver,
            ScreenshotFileWriter screenshotFileWriter,
            SupabaseScreenshotUploader uploader
    ) {
        super(new TestCaseModel(locator.id(), "Appium Screenshot", "Capture Appium screenshot", locator.description()));
        this.locator = locator;
        this.config = config;
        this.locatorResolver = locatorResolver;
        this.screenshotFileWriter = screenshotFileWriter;
        this.uploader = uploader;
    }

    @Override
    protected String executeTestLogic() {
        AndroidDriver driver = null;
        try {
            driver = AndroidDriverFactory.create(config);
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(20))
                    .until(ExpectedConditions.presenceOfElementLocated(locatorResolver.toBy(locator)));

            if (!locator.expectedText().isBlank()) {
                assertThat(element.getText()).containsIgnoringCase(locator.expectedText());
            }

            byte[] screenshot = driver.getScreenshotAs(OutputType.BYTES);
            ScreenshotArtifact artifact = screenshotFileWriter.write(locator.id(), screenshot);
            ScreenshotUploadResult uploadResult = uploader.upload(artifact);

            return uploadResult.uploaded()
                    ? "Appium screenshot captured and uploaded to Supabase: " + uploadResult.publicUrl()
                    : "Appium screenshot captured locally: " + uploadResult.localPath() + ". " + uploadResult.message();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @Override
    protected String failureDescription() {
        return "Failed to capture Appium screenshot for locator " + locator.id() + " using " + locator.strategy();
    }
}
