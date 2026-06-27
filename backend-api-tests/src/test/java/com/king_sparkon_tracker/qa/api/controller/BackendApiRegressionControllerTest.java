package com.king_sparkon_tracker.qa.api.controller;

import com.king_sparkon_tracker.qa.api.model.ApiScenario;
import com.king_sparkon_tracker.qa.api.view.ApiScenarioTestView;
import com.king_sparkon_tracker.qa.api.view.SvcApiScenarioReader;
import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestStatus;
import com.king_sparkon_tracker.qa.core.model.TestSuiteReport;
import com.king_sparkon_tracker.qa.core.view.ReportFileWriter;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BackendApiRegressionControllerTest {

    private static final String SCENARIO_FILE = "scenarios/backend-api-test-cases.svc";
    private static final List<TestExecutionResponse> RESPONSES = new CopyOnWriteArrayList<>();
    private static final Instant STARTED_AT = Instant.now();
    private static String authToken;

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = System.getProperty("api.baseUrl", "http://localhost:8080");
        authToken = System.getProperty("api.authToken", "").trim();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    static Stream<ApiScenario> apiScenarios() {
        return SvcApiScenarioReader.read(SCENARIO_FILE).stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("apiScenarios")
    void controllerShouldExecuteApiScenarioAndRecordReportResponse(ApiScenario scenario) {
        if (scenario.requiresAuth() && authToken.isBlank()) {
            TestExecutionResponse skipped = new TestExecutionResponse(
                    scenario.id(),
                    "Backend API - " + scenario.method() + " " + scenario.path(),
                    scenario.name(),
                    "Skipped because api.authToken was not supplied",
                    TestStatus.SKIPPED,
                    1,
                    "",
                    Instant.now()
            );
            RESPONSES.add(skipped);
            return;
        }

        TestExecutionResponse response = new ApiScenarioTestView(scenario, authToken).buildTest();
        RESPONSES.add(response);

        assertThat(response.status())
                .as(response.errorMessage())
                .isEqualTo(TestStatus.PASSED);
    }

    @AfterAll
    static void writeHtmlReport() {
        TestSuiteReport report = new TestSuiteReport(
                "King Sparkon Backend API Test Report",
                STARTED_AT,
                Instant.now(),
                RESPONSES
        );
        new ReportFileWriter().write(report, Path.of("target", "qa-report", "backend-api-report.html"));
    }
}
