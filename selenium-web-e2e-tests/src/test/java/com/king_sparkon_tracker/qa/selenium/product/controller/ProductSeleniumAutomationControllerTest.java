package com.king_sparkon_tracker.qa.selenium.product.controller;

import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestSuiteReport;
import com.king_sparkon_tracker.qa.core.view.HtmlReportView;
import com.king_sparkon_tracker.qa.selenium.product.model.ProductSeleniumScenario;
import com.king_sparkon_tracker.qa.selenium.product.model.ProductSeleniumScenarioCatalog;
import com.king_sparkon_tracker.qa.selenium.product.view.ProductSeleniumTestBuilder;
import com.king_sparkon_tracker.qa.selenium.product.view.SeleniumDriverFactory;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ProductSeleniumAutomationControllerTest {

    private static final String PRODUCT_SCENARIOS = "scenarios/product-selenium-test-cases.svc";

    private final ProductSeleniumScenarioCatalog catalog = new ProductSeleniumScenarioCatalog();

    @Test
    void productSeleniumScenariosAreDefinedAndReportable() throws IOException {
        Instant startedAt = Instant.now();
        String baseUrl = System.getProperty("ui.baseUrl", "http://localhost:3000");
        boolean browserExecutionEnabled = Boolean.parseBoolean(System.getProperty("ui.e2e.enabled", "false"));
        List<ProductSeleniumScenario> scenarios = catalog.load(PRODUCT_SCENARIOS);

        assertThat(scenarios).hasSizeGreaterThanOrEqualTo(8);
        assertThat(scenarios).allSatisfy(scenario -> {
            assertThat(scenario.id()).startsWith("SELENIUM-PRODUCT-");
            assertThat(scenario.path()).startsWith("/");
            assertThat(scenario.expectedText()).isNotBlank();
        });

        WebDriver driver = null;
        try {
            if (browserExecutionEnabled) {
                driver = new SeleniumDriverFactory().create();
            }

            WebDriver activeDriver = driver;
            List<TestExecutionResponse> responses = scenarios.stream()
                    .map(scenario -> new ProductSeleniumTestBuilder(
                            scenario,
                            activeDriver,
                            baseUrl,
                            browserExecutionEnabled
                    ).buildTest())
                    .toList();

            TestSuiteReport report = new TestSuiteReport(
                    "King Sparkon Product Selenium Automation Suite",
                    startedAt,
                    Instant.now(),
                    responses
            );

            Path reportPath = Path.of("target", "qa-report", "product-selenium-suite.html");
            Files.createDirectories(reportPath.getParent());
            Files.writeString(reportPath, new HtmlReportView().buildHtml(report));

            assertThat(report.totalTests()).isEqualTo(scenarios.size());
            assertThat(responses).noneMatch(TestExecutionResponse::failed);
            assertThat(reportPath).exists();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @Test
    void productSeleniumSuiteCoversRequiredProductJourneys() {
        List<ProductSeleniumScenario> scenarios = catalog.load(PRODUCT_SCENARIOS);
        Set<String> scenarioNames = scenarios.stream()
                .map(ProductSeleniumScenario::name)
                .collect(Collectors.toSet());

        assertThat(scenarioNames).contains(
                "Product catalogue page renders",
                "Product search accepts barcode or product name",
                "Barcode scanner accepts product barcode",
                "Unknown barcode shows controlled empty state",
                "Product pricing is visible",
                "Product inventory status is visible",
                "Product card detail navigation is available",
                "Dashboard exposes product summary"
        );
    }
}
