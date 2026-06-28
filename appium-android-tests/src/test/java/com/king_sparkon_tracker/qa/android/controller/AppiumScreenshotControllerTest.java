package com.king_sparkon_tracker.qa.android.controller;

import com.king_sparkon_tracker.qa.android.AppiumConfig;
import com.king_sparkon_tracker.qa.android.view.AppiumScreenshotTestView;
import com.king_sparkon_tracker.qa.android.view.SvcAndroidLocatorReader;
import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestStatus;
import com.king_sparkon_tracker.qa.core.model.TestSuiteReport;
import com.king_sparkon_tracker.qa.core.model.locator.ElementLocatorModel;
import com.king_sparkon_tracker.qa.core.view.ReportFileWriter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AppiumScreenshotControllerTest {

    private static final String LOCATOR_FILE = "scenarios/android-screenshot-locators.svc";
    private static final List<TestExecutionResponse> RESPONSES = new CopyOnWriteArrayList<>();
    private static final Instant STARTED_AT = Instant.now();

    static Stream<ElementLocatorModel> locators() {
        return SvcAndroidLocatorReader.read(LOCATOR_FILE).stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("locators")
    void controllerShouldCaptureAppiumScreenshotAndRecordReportResponse(ElementLocatorModel locator) {
        AppiumConfig config = AppiumConfig.fromSystemProperties();
        if (!config.hasLaunchTarget()) {
            TestExecutionResponse skipped = new TestExecutionResponse(
                    locator.id(),
                    "Appium Screenshot",
                    "Capture Appium screenshot",
                    "Skipped because appium.app or appium.appPackage + appium.appActivity was not supplied",
                    TestStatus.SKIPPED,
                    1,
                    "",
                    Instant.now()
            );
            RESPONSES.add(skipped);
            return;
        }

        TestExecutionResponse response = new AppiumScreenshotTestView(locator, config).buildTest();
        RESPONSES.add(response);

        assertThat(response.status())
                .as(response.errorMessage())
                .isEqualTo(TestStatus.PASSED);
    }

    @AfterAll
    static void writeHtmlReport() {
        TestSuiteReport report = new TestSuiteReport(
                "King Sparkon Appium Screenshot Report",
                STARTED_AT,
                Instant.now(),
                RESPONSES
        );
        new ReportFileWriter().write(report, Path.of("target", "qa-report", "appium-screenshot-report.html"));
    }
}
