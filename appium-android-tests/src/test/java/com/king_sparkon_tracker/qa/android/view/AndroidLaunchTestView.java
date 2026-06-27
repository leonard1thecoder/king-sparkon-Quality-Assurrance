package com.king_sparkon_tracker.qa.android.view;

import com.king_sparkon_tracker.qa.android.AndroidDriverFactory;
import com.king_sparkon_tracker.qa.android.AppiumConfig;
import com.king_sparkon_tracker.qa.android.model.AndroidScenarioModel;
import com.king_sparkon_tracker.qa.core.model.TestCaseModel;
import com.king_sparkon_tracker.qa.core.view.AbstractTestView;
import io.appium.java_client.android.AndroidDriver;

import static org.assertj.core.api.Assertions.assertThat;

public class AndroidLaunchTestView extends AbstractTestView {

    private final AndroidScenarioModel scenario;
    private final AppiumConfig config;

    public AndroidLaunchTestView(AndroidScenarioModel scenario, AppiumConfig config) {
        super(new TestCaseModel(scenario.id(), scenario.scenario(), scenario.name(), scenario.description()));
        this.scenario = scenario;
        this.config = config;
    }

    @Override
    protected String executeTestLogic() {
        AndroidDriver driver = null;
        try {
            driver = AndroidDriverFactory.create(config);
            assertThat(driver.getSessionId()).as("Appium session should start").isNotNull();
            return "Android app launched and Appium session was created";
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    @Override
    protected String failureDescription() {
        return scenario.name() + " failed. Check Appium server, emulator, app package/activity, or APK path.";
    }
}
