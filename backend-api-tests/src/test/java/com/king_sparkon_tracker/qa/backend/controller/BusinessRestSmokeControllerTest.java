package com.king_sparkon_tracker.qa.backend.controller;

import com.king_sparkon_tracker.qa.backend.model.BusinessRestScenario;
import com.king_sparkon_tracker.qa.backend.model.BusinessRestScenarioCatalog;
import com.king_sparkon_tracker.qa.backend.view.BusinessRestScenarioTestBuilder;
import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestStatus;
import com.king_sparkon_tracker.qa.core.model.TestSuiteReport;
import com.king_sparkon_tracker.qa.core.view.HtmlReportView;
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

class BusinessRestSmokeControllerTest {

    private final BusinessRestScenarioCatalog catalog = new BusinessRestScenarioCatalog();

    @Test
    void businessRestSmokeDefinitionsCoverEveryDomainAuthGate() {
        List<BusinessRestScenario> scenarios = catalog.loadScenarios(List.of("scenarios/rest/rest-business-smoke-test-cases.svc"));

        assertThat(scenarios).hasSize(8);
        assertThat(scenarios.stream().map(BusinessRestScenario::domain).collect(Collectors.toSet()))
                .containsExactlyInAnyOrder("TIPS", "CLAIMS", "RETURNABLES", "TRANSACTIONS");

        Map<String, Set<String>> caseTypesByDomain = scenarios.stream()
                .collect(Collectors.groupingBy(
                        BusinessRestScenario::domain,
                        Collectors.mapping(BusinessRestScenario::caseType, Collectors.toSet())
                ));

        assertThat(caseTypesByDomain).allSatisfy((domain, caseTypes) ->
                assertThat(caseTypes).contains("POSITIVE", "NEGATIVE")
        );
    }

    @Test
    void executeBusinessRestSmokeAndWriteReport() throws IOException {
        List<BusinessRestScenario> scenarios = catalog.loadScenarios(List.of("scenarios/rest/rest-business-smoke-test-cases.svc"));
        boolean executeRestFlows = Boolean.parseBoolean(System.getProperty("api.executeRestFlows", "false"));
        String baseUrl = System.getProperty("api.baseUrl", "http://localhost:8080");
        String authToken = System.getProperty("api.authToken", "");

        Instant startedAt = Instant.now();
        List<TestExecutionResponse> responses = executeRestFlows
                ? scenarios.stream()
                        .map(scenario -> new BusinessRestScenarioTestBuilder(scenario, baseUrl, authToken).buildTest())
                        .toList()
                : scenarios.stream()
                        .map(scenario -> new TestExecutionResponse(
                                scenario.id(),
                                scenario.domain() + " " + scenario.caseType(),
                                scenario.name(),
                                "Smoke definition validated. Runtime REST execution disabled. Run with -Dapi.executeRestFlows=true.",
                                TestStatus.SKIPPED,
                                1,
                                "",
                                Instant.now()
                        ))
                        .toList();

        TestSuiteReport report = new TestSuiteReport(
                "King Sparkon REST Business Smoke Suite",
                startedAt,
                Instant.now(),
                responses
        );

        Path reportPath = Path.of("target", "qa-report", "rest-business-smoke.html");
        Files.createDirectories(reportPath.getParent());
        Files.writeString(reportPath, new HtmlReportView().buildHtml(report));

        assertThat(reportPath).exists();
        assertThat(responses).noneMatch(TestExecutionResponse::failed);
    }
}
