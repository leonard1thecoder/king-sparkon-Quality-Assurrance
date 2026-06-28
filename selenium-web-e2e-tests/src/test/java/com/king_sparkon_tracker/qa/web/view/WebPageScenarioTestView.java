package com.king_sparkon_tracker.qa.web.view;

import com.king_sparkon_tracker.qa.core.model.TestCaseModel;
import com.king_sparkon_tracker.qa.core.view.AbstractTestView;
import com.king_sparkon_tracker.qa.web.WebDriverFactory;
import com.king_sparkon_tracker.qa.web.model.WebScenarioModel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class WebPageScenarioTestView extends AbstractTestView {

    private final WebScenarioModel scenario;
    private final String baseUrl;

    public WebPageScenarioTestView(WebScenarioModel scenario, String baseUrl) {
        super(new TestCaseModel(
                scenario.id(),
                "Website UI - " + scenario.path(),
                scenario.name(),
                "Expected page content: " + scenario.expectedContent()
        ));
        this.scenario = scenario;
        this.baseUrl = baseUrl == null || baseUrl.isBlank() ? "http://localhost:3000" : baseUrl;
    }

    @Override
    protected String executeTestLogic() {
        WebDriver driver = null;
        try {
            driver = WebDriverFactory.create();
            driver.get(baseUrl + scenario.path());

            new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

            String bodyText = driver.findElement(By.tagName("body")).getText();
            String title = driver.getTitle();

            assertThat(title + "\n" + bodyText)
                    .as("%s should contain expected content [%s]", scenario.path(), scenario.expectedContent())
                    .containsIgnoringCase(scenario.expectedContent());

            return scenario.path() + " rendered and contained expected content: " + scenario.expectedContent();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @Override
    protected String failureDescription() {
        return scenario.path() + " failed to render expected content: " + scenario.expectedContent();
    }
}
