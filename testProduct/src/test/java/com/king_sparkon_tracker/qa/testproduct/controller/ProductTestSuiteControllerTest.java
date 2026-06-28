package com.king_sparkon_tracker.qa.testproduct.controller;

import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestSuiteReport;
import com.king_sparkon_tracker.qa.core.view.HtmlReportView;
import com.king_sparkon_tracker.qa.testproduct.model.ProductScenarioCatalog;
import com.king_sparkon_tracker.qa.testproduct.model.ProductTestCaseDefinition;
import com.king_sparkon_tracker.qa.testproduct.model.ProductTestStep;
import com.king_sparkon_tracker.qa.testproduct.view.ProductTestBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTestSuiteControllerTest {

    private final ProductScenarioCatalog catalog = new ProductScenarioCatalog();

    @Test
    void productQaDefinitionsAreCompleteAndReportable() throws IOException {
        Instant startedAt = Instant.now();
        List<ProductTestCaseDefinition> testCases = catalog.loadTestCases("scenarios/product-test-cases.svc");
        List<ProductTestStep> steps = catalog.loadSteps("scenarios/product-test-steps.scv");

        assertThat(testCases).hasSizeGreaterThanOrEqualTo(8);
        assertThat(steps).hasSizeGreaterThanOrEqualTo(testCases.size());

        List<TestExecutionResponse> responses = testCases.stream()
                .map(testCase -> new ProductTestBuilder(testCase, steps).buildTest())
                .toList();

        assertThat(responses).allMatch(TestExecutionResponse::passed);

        TestSuiteReport report = new TestSuiteReport(
                "King Sparkon Product QA Automation Suite",
                startedAt,
                Instant.now(),
                responses
        );

        Path reportPath = Path.of("target", "qa-report", "product-test-suite.html");
        Files.createDirectories(reportPath.getParent());
        Files.writeString(reportPath, new HtmlReportView().buildHtml(report));

        assertThat(report.totalTests()).isEqualTo(testCases.size());
        assertThat(report.passedTests()).isEqualTo(testCases.size());
        assertThat(reportPath).exists();
    }

    @Test
    void productSuiteCoversCatalogueInventoryBarcodeAndPricingRisk() {
        List<ProductTestCaseDefinition> testCases = catalog.loadTestCases("scenarios/product-test-cases.svc");
        Set<String> features = testCases.stream()
                .map(ProductTestCaseDefinition::feature)
                .collect(Collectors.toSet());

        assertThat(features).contains(
                "Product Catalogue",
                "Inventory",
                "Barcode Lookup",
                "Pricing",
                "Negative Validation"
        );
    }

    @Test
    void everyProductTestCaseHasOrderedSteps() {
        List<ProductTestCaseDefinition> testCases = catalog.loadTestCases("scenarios/product-test-cases.svc");
        List<ProductTestStep> steps = catalog.loadSteps("scenarios/product-test-steps.scv");
        Map<String, List<ProductTestStep>> stepsByCase = steps.stream()
                .collect(Collectors.groupingBy(ProductTestStep::testCaseId));

        for (ProductTestCaseDefinition testCase : testCases) {
            List<ProductTestStep> caseSteps = stepsByCase.getOrDefault(testCase.id(), List.of());
            assertThat(caseSteps)
                    .as(testCase.id() + " must have documented automation steps")
                    .isNotEmpty();

            assertThat(caseSteps.stream().map(ProductTestStep::order).toList())
                    .as(testCase.id() + " steps must start at 1 and remain ordered")
                    .contains(1);
        }
    }
}
