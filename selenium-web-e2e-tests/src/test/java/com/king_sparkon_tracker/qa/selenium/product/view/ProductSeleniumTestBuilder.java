package com.king_sparkon_tracker.qa.selenium.product.view;

import com.king_sparkon_tracker.qa.core.model.TestCaseModel;
import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestStatus;
import com.king_sparkon_tracker.qa.core.view.TestBuilder;
import com.king_sparkon_tracker.qa.selenium.product.model.ProductSeleniumScenario;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;

public class ProductSeleniumTestBuilder implements TestBuilder {

    private final ProductSeleniumScenario scenario;
    private final WebDriver driver;
    private final String baseUrl;
    private final boolean browserExecutionEnabled;

    public ProductSeleniumTestBuilder(
            ProductSeleniumScenario scenario,
            WebDriver driver,
            String baseUrl,
            boolean browserExecutionEnabled
    ) {
        this.scenario = scenario;
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.browserExecutionEnabled = browserExecutionEnabled;
    }

    @Override
    public TestExecutionResponse buildTest() {
        Instant startedAt = Instant.now();
        TestCaseModel model = new TestCaseModel(
                scenario.id(),
                "Product Selenium E2E",
                scenario.name(),
                scenario.description()
        );

        if (!browserExecutionEnabled) {
            return new TestExecutionResponse(
                    scenario.id(),
                    "Product Selenium E2E",
                    scenario.name(),
                    "Skipped live browser execution. Run with -Dui.e2e.enabled=true when the Next.js app is running.",
                    TestStatus.SKIPPED,
                    1,
                    "",
                    Instant.now()
            );
        }

        try {
            openScenarioPath();
            runAction();
            return TestExecutionResponse.passed(
                    model,
                    elapsedMs(startedAt),
                    "Product Selenium scenario passed: " + scenario.id()
            );
        } catch (RuntimeException exception) {
            return TestExecutionResponse.failed(
                    model,
                    elapsedMs(startedAt),
                    "Product Selenium scenario failed: " + scenario.id(),
                    exception
            );
        }
    }

    private void openScenarioPath() {
        driver.get(URI.create(baseUrl).resolve(scenario.path()).toString());
        waitFor().until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
    }

    private void runAction() {
        String action = scenario.actionType().toUpperCase(Locale.ROOT);
        switch (action) {
            case "ASSERT_TEXT" -> assertPageContainsExpectedText();
            case "ASSERT_VISIBLE" -> assertVisible();
            case "TYPE_AND_ASSERT_TEXT" -> typeAndAssertText();
            case "CLICK_AND_ASSERT_TEXT" -> clickAndAssertText();
            default -> throw new IllegalArgumentException("Unsupported product Selenium action: " + scenario.actionType());
        }
    }

    private void assertVisible() {
        WebElement element = waitFor().until(ExpectedConditions.visibilityOfElementLocated(locator()));
        if (!scenario.expectedText().isBlank()) {
            assertContainsAny(element.getText() + " " + pageText(), scenario.expectedText());
        }
    }

    private void typeAndAssertText() {
        WebElement element = waitFor().until(ExpectedConditions.visibilityOfElementLocated(locator()));
        element.clear();
        element.sendKeys(scenario.inputValue());
        element.sendKeys(Keys.ENTER);
        assertPageContainsExpectedText();
    }

    private void clickAndAssertText() {
        WebElement element = waitFor().until(ExpectedConditions.elementToBeClickable(locator()));
        element.click();
        assertPageContainsExpectedText();
    }

    private void assertPageContainsExpectedText() {
        if (scenario.expectedText().isBlank()) {
            throw new IllegalArgumentException(scenario.id() + " must define expectedText for ASSERT_TEXT-style validation");
        }
        assertContainsAny(pageText(), scenario.expectedText());
    }

    private void assertContainsAny(String actualText, String expectedText) {
        String safeActual = actualText == null ? "" : actualText.toLowerCase(Locale.ROOT);
        boolean matched = Arrays.stream(expectedText.split(";"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(safeActual::contains);

        if (!matched) {
            throw new AssertionError("Expected page to contain one of [" + expectedText + "] but actual page text was: " + trimForError(actualText));
        }
    }

    private String pageText() {
        return waitFor().until(ExpectedConditions.presenceOfElementLocated(By.tagName("body"))).getText();
    }

    private By locator() {
        String strategy = scenario.locatorStrategy().toLowerCase(Locale.ROOT);
        String value = scenario.locatorValue();
        return switch (strategy) {
            case "id" -> By.id(value);
            case "name" -> By.name(value);
            case "css" -> By.cssSelector(value);
            case "class-name" -> By.className(value);
            case "tag-name" -> By.tagName(value);
            case "link-text" -> By.linkText(value);
            case "partial-link-text" -> By.partialLinkText(value);
            case "xpath", "full-xpath" -> By.xpath(value);
            default -> throw new IllegalArgumentException("Unsupported Selenium locator strategy: " + scenario.locatorStrategy());
        };
    }

    private WebDriverWait waitFor() {
        return new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    private long elapsedMs(Instant startedAt) {
        return Math.max(1, Duration.between(startedAt, Instant.now()).toMillis());
    }

    private String trimForError(String text) {
        if (text == null) {
            return "";
        }
        return text.length() <= 500 ? text : text.substring(0, 500) + "...";
    }
}
