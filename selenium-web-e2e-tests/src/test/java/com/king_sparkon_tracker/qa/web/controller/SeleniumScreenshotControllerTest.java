package com.king_sparkon_tracker.qa.web.controller;

import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestStatus;
import com.king_sparkon_tracker.qa.core.model.TestSuiteReport;
import com.king_sparkon_tracker.qa.core.model.locator.ElementLocatorModel;
import com.king_sparkon_tracker.qa.core.view.ReportFileWriter;
import com.king_sparkon_tracker.qa.web.view.SeleniumScreenshotTestView;
import com.king_sparkon_tracker.qa.web.view.SvcElementLocatorReader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SeleniumScreenshotControllerTest {

    private static final String LOCATOR_FILE = "scenarios/web-screenshot-locators.svc";
    private static final List<TestExecutionResponse> RESPONSES = new CopyOnWriteArrayList<>();
    private static final Instant STARTED_AT = Instant.now();
    private static final String BASE_URL = System.getProperty("ui.baseUrl", "http://localhost:3000");

    static Stream<ElementLocatorModel> locators() {
        return SvcElementLocatorReader.read(LOCATOR_FILE).stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("locators")
    void controllerShouldCaptureScreenshotAndRecordReportResponse(ElementLocatorModel locator) {
        TestExecutionResponse response = new SeleniumScreenshotTestView(locator, BASE_URL).buildTest();
        RESPONSES.add(response);

        assertThat(response.status())
                .as(response.errorMessage())
                .isEqualTo(TestStatus.PASSED);
    }

    @AfterAll
    static void writeHtmlReport() {
        TestSuiteReport report = new TestSuiteReport(
                "King Sparkon Selenium Screenshot Report",
                STARTED_AT,
                Instant.now(),
                RESPONSES
        );
        new ReportFileWriter().write(report, Path.of("target", "qa-report", "selenium-screenshot-report.html"));
    }
}
