package com.king_sparkon_tracker.qa.core.controller;

import com.king_sparkon_tracker.qa.core.model.TestCaseModel;
import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestStatus;
import com.king_sparkon_tracker.qa.core.model.TestSuiteReport;
import com.king_sparkon_tracker.qa.core.view.ReportFileWriter;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlReportControllerTest {

    @Test
    void controllerShouldGenerateHtmlReportFromTestResponses() {
        Instant startedAt = Instant.now();
        TestCaseModel loginCase = new TestCaseModel("WEB-LOGIN-001", "Authentication", "Login page renders", "Email and password fields are visible");
        TestCaseModel apiCase = new TestCaseModel("API-HEALTH-001", "Backend API", "Health endpoint", "Backend health should return HTTP 200");

        List<TestExecutionResponse> responses = List.of(
                TestExecutionResponse.passed(loginCase, 321, "Login page rendered with required fields"),
                new TestExecutionResponse(apiCase.id(), apiCase.scenario(), apiCase.name(), "Health endpoint returned HTTP 500", TestStatus.FAILED, 214, "Expected HTTP 200 but got HTTP 500", Instant.now())
        );

        TestSuiteReport report = new TestSuiteReport("King Sparkon Sample MVC QA Report", startedAt, Instant.now(), responses);
        Path reportPath = new ReportFileWriter().write(report, Path.of("target", "qa-report", "sample-index.html"));

        assertThat(Files.exists(reportPath)).isTrue();
        assertThat(reportPath).hasFileName("sample-index.html");
    }
}
