package com.king_sparkon_tracker.qa.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PublicPageSmokeTest {

    private static final String SCENARIO_FILE = "scenarios/web-ui-test-cases.svc";
    private static final String BASE_URL = System.getProperty("ui.baseUrl", "http://localhost:3000");
    private WebDriver driver;

    static Stream<WebScenario> scenarios() {
        return SvcWebScenarioReader.read(SCENARIO_FILE).stream();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void pageShouldRenderExpectedContent(WebScenario scenario) {
        Assumptions.assumeFalse(scenario.requiresAuth(), "Skipping auth-required UI scenario until login/session helper is added: " + scenario.id());

        driver = WebDriverFactory.create();
        driver.get(BASE_URL + scenario.path());

        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        String bodyText = driver.findElement(By.tagName("body")).getText();
        String title = driver.getTitle();

        assertThat(title + "\n" + bodyText)
                .as("%s should contain expected content [%s]", scenario.path(), scenario.expectedContent())
                .containsIgnoringCase(scenario.expectedContent());
    }
}
