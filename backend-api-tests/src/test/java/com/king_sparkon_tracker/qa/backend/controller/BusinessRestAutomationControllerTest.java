package com.king_sparkon_tracker.qa.backend.controller;

import com.king_sparkon_tracker.qa.backend.model.BusinessRestScenario;
import com.king_sparkon_tracker.qa.backend.model.BusinessRestScenarioCatalog;
import com.king_sparkon_tracker.qa.backend.model.BusinessRestStep;
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

class BusinessRestAutomationControllerTest {

    private static final List<String> SCENARIO_FILES = List.of(
            "scenarios/rest/tips-rest-test-cases.svc",
            "scenarios/rest/claims-rest-test-cases.svc",
            "scenarios/rest/returnables-rest-test-cases.svc",
            "scenarios/rest/transactions-rest-test-cases.svc"
    );

    private final BusinessRestScenarioCatalog catalog = new BusinessRestScenarioCatalog();

    @Test
    void businessRestDefinitionsCoverPositiveAndNegativeCases() {
        List<BusinessRestScenario> scenarios = catalog.loadScenarios(SCENARIO_FILES);
        List<BusinessRestStep> steps = catalog.loadSteps("scenarios/rest/rest-business-flow-test-steps.scv");

        assertThat(scenarios).hasSizeGreaterThanOrEqualTo(30);
        assertThat(scenarios.stream().map(BusinessRestScenario::domain).collect(Collectors.toSet()))
                .contains("TIPS", "CLAIMS", "RETURNABLES", "TRANSACTIONS");

        Map<String, Set<String>> caseTypesByDomain = scenarios.stream()
                .collect(Collectors.groupingBy(
                        BusinessRestScenario::domain,
                        Collectors.mapping(BusinessRestScenario::caseType, Collectors.toSet())
                ));

        assertThat(caseTypesByDomain).allSatisfy((domain, caseTypes) ->
                assertThat(caseTypes)
                        .as(domain + " must cover positive and negative REST paths")
                        .contains("POSITIVE", "NEGATIVE")
        );

        Map<String, List<BusinessRestStep>> stepsByCase = steps.stream()
                .collect(Collectors.groupingBy(BusinessRestStep::testCaseId));

        for (BusinessRestScenario scenario : scenarios) {
            assertThat(stepsByCase.getOrDefault(scenario.id(), List.of()))
                    .as(scenario.id() + " must have SCV test steps")
                    .isNotEmpty();
        }
    }

    @Test
    void executeBusinessRestFlowsAndWriteReport() throws IOException {
        List<BusinessRestScenario> scenarios = catalog.loadScenarios(SCENARIO_FILES);
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
                                "Definition validated. Runtime REST execution disabled. Run with -Dapi.executeRestFlows=true.",
                                TestStatus.SKIPPED,
                                1,
                                "",
                                Instant.now()
                        ))
                        .toList();

        TestSuiteReport report = new TestSuiteReport(
                "King Sparkon REST Business Flow Automation Suite",
                startedAt,
                Instant.now(),
                responses
        );

        Path reportPath = Path.of("target", "qa-report", "rest-business-flows.html");
        Files.createDirectories(reportPath.getParent());
        Files.writeString(reportPath, new HtmlReportView().buildHtml(report));

        assertThat(reportPath).exists();
        assertThat(responses).noneMatch(TestExecutionResponse::failed);
    }
}
