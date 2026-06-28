package com.king_sparkon_tracker.qa.security.controller;

import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestStatus;
import com.king_sparkon_tracker.qa.core.model.TestSuiteReport;
import com.king_sparkon_tracker.qa.core.view.ReportFileWriter;
import com.king_sparkon_tracker.qa.security.model.SecurityScenarioModel;
import com.king_sparkon_tracker.qa.security.view.SecurityBaselineReadinessView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class BaselineReadinessControllerTest {

    private static final Instant STARTED_AT = Instant.now();
    private static final List<TestExecutionResponse> RESPONSES = new CopyOnWriteArrayList<>();

    @Test
    void controllerShouldRecordBaselineReadiness() {
        SecurityScenarioModel scenario = new SecurityScenarioModel(
                "SEC-BASELINE-001",
                "Baseline Setup",
                "ZAP readiness",
                "Target URL is valid and configuration file exists"
        );

        TestExecutionResponse response = new SecurityBaselineReadinessView(
                scenario,
                System.getProperty("security.targetUrl", "http://localhost:3000")
        ).buildTest();
        RESPONSES.add(response);

        assertThat(response.status()).as(response.errorMessage()).isEqualTo(TestStatus.PASSED);
    }

    @AfterAll
    static void writeHtmlReport() {
        TestSuiteReport report = new TestSuiteReport("King Sparkon Baseline Report", STARTED_AT, Instant.now(), RESPONSES);
        new ReportFileWriter().write(report, Path.of("target", "qa-report", "security-baseline-report.html"));
    }
}
