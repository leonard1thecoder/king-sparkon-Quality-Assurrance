package com.king_sparkon_tracker.qa.web.view;

import com.king_sparkon_tracker.qa.core.model.TestCaseModel;
import com.king_sparkon_tracker.qa.core.model.locator.ElementLocatorModel;
import com.king_sparkon_tracker.qa.core.model.screenshot.ScreenshotArtifact;
import com.king_sparkon_tracker.qa.core.model.screenshot.ScreenshotUploadResult;
import com.king_sparkon_tracker.qa.core.view.AbstractTestView;
import com.king_sparkon_tracker.qa.core.view.screenshot.ScreenshotFileWriter;
import com.king_sparkon_tracker.qa.core.view.storage.SupabaseScreenshotUploader;
import com.king_sparkon_tracker.qa.web.WebDriverFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class SeleniumScreenshotTestView extends AbstractTestView {

    private final ElementLocatorModel locator;
    private final String baseUrl;
    private final SeleniumLocatorResolver locatorResolver;
    private final ScreenshotFileWriter screenshotFileWriter;
    private final SupabaseScreenshotUploader uploader;

    public SeleniumScreenshotTestView(ElementLocatorModel locator, String baseUrl) {
        this(locator, baseUrl, new SeleniumLocatorResolver(), new ScreenshotFileWriter(), new SupabaseScreenshotUploader());
    }

    public SeleniumScreenshotTestView(
            ElementLocatorModel locator,
            String baseUrl,
            SeleniumLocatorResolver locatorResolver,
            ScreenshotFileWriter screenshotFileWriter,
            SupabaseScreenshotUploader uploader
    ) {
        super(new TestCaseModel(locator.id(), "Selenium Screenshot - " + locator.pagePath(), "Capture Selenium screenshot", locator.description()));
        this.locator = locator;
        this.baseUrl = baseUrl == null || baseUrl.isBlank() ? "http://localhost:3000" : baseUrl;
        this.locatorResolver = locatorResolver;
        this.screenshotFileWriter = screenshotFileWriter;
        this.uploader = uploader;
    }

    @Override
    protected String executeTestLogic() {
        WebDriver driver = null;
        try {
            driver = WebDriverFactory.create();
            driver.get(baseUrl + locator.pagePath());
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement element = wait.until((ExpectedCondition<WebElement>) currentDriver -> locatorResolver.find(currentDriver, locator));

            if (!locator.expectedText().isBlank()) {
                assertThat(element.getText()).containsIgnoringCase(locator.expectedText());
            }

            highlight(driver, element);
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            ScreenshotArtifact artifact = screenshotFileWriter.write(locator.id(), screenshot);
            ScreenshotUploadResult uploadResult = uploader.upload(artifact);

            return uploadResult.uploaded()
                    ? "Screenshot captured and uploaded to Supabase: " + uploadResult.publicUrl()
                    : "Screenshot captured locally: " + uploadResult.localPath() + ". " + uploadResult.message();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @Override
    protected String failureDescription() {
        return "Failed to capture Selenium screenshot for locator " + locator.id() + " using " + locator.strategy();
    }

    private void highlight(WebDriver driver, WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.outline='4px solid #f97316'; arguments[0].style.boxShadow='0 0 0 8px rgba(249,115,22,.25)';",
                    element
            );
        } catch (RuntimeException ignored) {
            // Screenshot should still be taken even if highlight fails.
        }
    }
}
