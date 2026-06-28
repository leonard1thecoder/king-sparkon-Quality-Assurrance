package com.king_sparkon_tracker.qa.security.view;

import com.king_sparkon_tracker.qa.core.model.TestCaseModel;
import com.king_sparkon_tracker.qa.core.view.AbstractTestView;
import com.king_sparkon_tracker.qa.security.model.SecurityScenarioModel;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class SecurityBaselineReadinessView extends AbstractTestView {

    private final SecurityScenarioModel scenario;
    private final String targetUrl;

    public SecurityBaselineReadinessView(SecurityScenarioModel scenario, String targetUrl) {
        super(new TestCaseModel(scenario.id(), scenario.scenario(), scenario.name(), scenario.description()));
        this.scenario = scenario;
        this.targetUrl = targetUrl == null ? "" : targetUrl.trim();
    }

    @Override
    protected String executeTestLogic() {
        URI uri = URI.create(targetUrl);
        assertThat(uri.getScheme()).as("security.targetUrl must use http or https").isIn("http", "https");
        assertThat(uri.getHost()).as("security.targetUrl must include a host").isNotBlank();

        Path zapConfig = Path.of("src", "test", "resources", "zap", "zap-baseline.conf");
        assertThat(Files.exists(zapConfig)).as("ZAP baseline config must exist").isTrue();

        return "Security baseline is ready for target " + targetUrl + " and ZAP config exists";
    }

    @Override
    protected String failureDescription() {
        return scenario.name() + " failed. Check security.targetUrl and zap-baseline.conf.";
    }
}
