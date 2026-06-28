package com.king_sparkon_tracker.qa.android.controller;

import com.king_sparkon_tracker.qa.android.AppiumConfig;
import com.king_sparkon_tracker.qa.android.model.AndroidScenarioModel;
import com.king_sparkon_tracker.qa.android.view.AndroidLaunchTestView;
import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestStatus;
import com.king_sparkon_tracker.qa.core.model.TestSuiteReport;
import com.king_sparkon_tracker.qa.core.view.ReportFileWriter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class AppiumAndroidControllerTest {

    private static final Instant STARTED_AT = Instant.now();
    private static final List<TestExecutionResponse> RESPONSES = new CopyOnWriteArrayList<>();

    @Test
    void controllerShouldLaunchAndroidAppAndRecordReportResponse() {
        AppiumConfig config = AppiumConfig.fromSystemProperties();
        AndroidScenarioModel scenario = new AndroidScenarioModel(
                "ANDROID-LAUNCH-001",
                "Android App Launch",
                "Android app launches on emulator or device",
                "Appium should start a session using APK path or package/activity"
        );

        if (!config.hasLaunchTarget()) {
            TestExecutionResponse skipped = new TestExecutionResponse(
                    scenario.id(),
                    scenario.scenario(),
                    scenario.name(),
                    "Skipped because appium.app or appium.appPackage + appium.appActivity was not supplied",
                    TestStatus.SKIPPED,
                    1,
                    "",
                    Instant.now()
            );
            RESPONSES.add(skipped);
            return;
        }

        TestExecutionResponse response = new AndroidLaunchTestView(scenario, config).buildTest();
        RESPONSES.add(response);

        assertThat(response.status())
                .as(response.errorMessage())
                .isEqualTo(TestStatus.PASSED);
    }

    @AfterAll
    static void writeHtmlReport() {
        TestSuiteReport report = new TestSuiteReport(
                "King Sparkon Appium Android Test Report",
                STARTED_AT,
                Instant.now(),
                RESPONSES
        );
        new ReportFileWriter().write(report, Path.of("target", "qa-report", "appium-android-report.html"));
    }
}
