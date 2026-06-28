package com.king_sparkon_tracker.qa.web.controller;

import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestStatus;
import com.king_sparkon_tracker.qa.core.model.TestSuiteReport;
import com.king_sparkon_tracker.qa.core.view.ReportFileWriter;
import com.king_sparkon_tracker.qa.web.model.WebScenarioModel;
import com.king_sparkon_tracker.qa.web.view.SvcWebScenarioReaderMvc;
import com.king_sparkon_tracker.qa.web.view.WebPageScenarioTestView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SeleniumWebE2EControllerTest {

    private static final String SCENARIO_FILE = "scenarios/web-ui-test-cases.svc";
    private static final List<TestExecutionResponse> RESPONSES = new CopyOnWriteArrayList<>();
    private static final Instant STARTED_AT = Instant.now();
    private static final String BASE_URL = System.getProperty("ui.baseUrl", "http://localhost:3000");

    static Stream<WebScenarioModel> scenarios() {
        return SvcWebScenarioReaderMvc.read(SCENARIO_FILE).stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void controllerShouldExecuteWebScenarioAndRecordReportResponse(WebScenarioModel scenario) {
        if (scenario.requiresAuth()) {
            TestExecutionResponse skipped = new TestExecutionResponse(
                    scenario.id(),
                    "Website UI - " + scenario.path(),
                    scenario.name(),
                    "Skipped until authenticated Selenium session helper is implemented",
                    TestStatus.SKIPPED,
                    1,
                    "",
                    Instant.now()
            );
            RESPONSES.add(skipped);
            return;
        }

        TestExecutionResponse response = new WebPageScenarioTestView(scenario, BASE_URL).buildTest();
        RESPONSES.add(response);

        assertThat(response.status())
                .as(response.errorMessage())
                .isEqualTo(TestStatus.PASSED);
    }

    @AfterAll
    static void writeHtmlReport() {
        TestSuiteReport report = new TestSuiteReport(
                "King Sparkon Selenium Website E2E Report",
                STARTED_AT,
                Instant.now(),
                RESPONSES
        );
        new ReportFileWriter().write(report, Path.of("target", "qa-report", "selenium-web-report.html"));
    }
}
