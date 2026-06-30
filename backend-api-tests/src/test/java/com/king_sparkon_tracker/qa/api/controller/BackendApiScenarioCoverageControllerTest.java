package com.king_sparkon_tracker.qa.api.controller;

import com.king_sparkon_tracker.qa.api.model.ApiScenario;
import com.king_sparkon_tracker.qa.api.view.SvcApiScenarioReader;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class BackendApiScenarioCoverageControllerTest {

    private static final String SCENARIO_FILE = "scenarios/backend-api-test-cases.svc";
    private static final List<ApiScenario> SCENARIOS = SvcApiScenarioReader.read(SCENARIO_FILE);
    private static final Set<Integer> AUTH_WALL_STATUSES = Set.of(401, 403);
    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    @Test
    void controllerShouldHaveUniqueScenarioIds() {
        List<String> duplicateScenarioIds = SCENARIOS.stream()
                .collect(Collectors.groupingBy(ApiScenario::id, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > 1)
                .map(Map.Entry::getKey)
                .sorted()
                .toList();

        assertThat(duplicateScenarioIds)
                .as("Duplicate API scenario ids hide coverage gaps in the QA report")
                .isEmpty();
    }

    @Test
    void controllerShouldCoverBackendFeatureSurfaceFromCurrentContract() {
        Map<String, Predicate<ApiScenario>> requiredCoverage = new LinkedHashMap<>();
        requiredCoverage.put("operations health", scenario -> scenario.path().equals("/actuator/health"));
        requiredCoverage.put("protected OpenAPI contract", scenario -> scenario.path().equals("/v3/api-docs") && scenario.expectedStatuses().containsAll(AUTH_WALL_STATUSES));
        requiredCoverage.put("protected Swagger UI", scenario -> scenario.path().equals("/swagger-ui.html") && scenario.expectedStatuses().containsAll(AUTH_WALL_STATUSES));
        requiredCoverage.put("user dashboard auth wall", scenario -> scenario.path().equals("/api/user-dashboard") && !scenario.requiresAuth());
        requiredCoverage.put("user dashboard positive path", scenario -> scenario.path().equals("/api/user-dashboard") && scenario.requiresAuth());
        requiredCoverage.put("business dashboard cards", scenario -> scenario.path().equals("/api/user-dashboard/businesses"));
        requiredCoverage.put("business workers for tips", scenario -> scenario.path().contains("/workers"));
        requiredCoverage.put("business events", scenario -> scenario.path().contains("/events") && scenario.path().contains("businesses"));
        requiredCoverage.put("worker tip payment link", scenario -> scenario.path().equals("/api/user-dashboard/tips"));
        requiredCoverage.put("ticket events", scenario -> scenario.path().equals("/api/v1/tickets/events"));
        requiredCoverage.put("ticket purchase", scenario -> scenario.path().equals("/api/v1/tickets/me/purchase"));
        requiredCoverage.put("my tickets auth wall", scenario -> scenario.path().equals("/api/v1/tickets/me/tickets") && !scenario.requiresAuth());
        requiredCoverage.put("my tickets positive path", scenario -> scenario.path().equals("/api/v1/tickets/me/tickets") && scenario.requiresAuth());
        requiredCoverage.put("ticket boosts", scenario -> scenario.path().contains("/boosts"));
        requiredCoverage.put("business account summary", scenario -> scenario.path().equals("/api/business-account/summary"));
        requiredCoverage.put("business account ledger", scenario -> scenario.path().equals("/api/business-account/ledger"));
        requiredCoverage.put("business account top-up", scenario -> scenario.path().equals("/api/business-account/top-ups"));

        requiredCoverage.forEach((area, rule) -> assertThat(SCENARIOS.stream().anyMatch(rule))
                .as("Missing backend API scenario coverage for %s", area)
                .isTrue());
    }

    @Test
    void controllerShouldKeepPaymentMessagingAndMoneyMovementSafeByDefault() {
        List<ApiScenario> unsafeScenarios = SCENARIOS.stream()
                .filter(this::isSensitiveWriteScenario)
                .filter(scenario -> !scenario.requiresAuth())
                .filter(scenario -> scenario.expectedStatuses().stream().noneMatch(AUTH_WALL_STATUSES::contains))
                .toList();

        assertThat(unsafeScenarios)
                .as("Payment, tip, ticket purchase, boost, and top-up scenarios must be auth-walled unless explicitly run with a token")
                .isEmpty();
    }

    @Test
    void controllerShouldKeepPublicSmokeCoverageRunnableWithoutSecrets() {
        List<String> publicSmokeIds = List.of("API-HEALTH-001", "TICKETS-PUBLIC-EVENTS-001");

        assertThat(SCENARIOS)
                .filteredOn(scenario -> publicSmokeIds.contains(scenario.id()))
                .allSatisfy(scenario -> {
                    assertThat(scenario.requiresAuth())
                            .as("%s must run without api.authToken for pre-prod smoke confidence", scenario.id())
                            .isFalse();
                    assertThat(scenario.expectedStatuses())
                            .as("%s must include at least one success status", scenario.id())
                            .containsAnyOf(200, 204, 302);
                });
    }

    @Test
    void controllerShouldParseMultiStatusExpectationsForCloudRunAndSpringSecurityDifferences() {
        ApiScenario swaggerScenario = findScenario("API-SWAGGER-AUTH-001");
        ApiScenario dashboardAuthWallScenario = findScenario("USER-DASHBOARD-AUTH-001");

        assertThat(swaggerScenario.expectedStatuses())
                .as("Production Swagger can be protected by Spring Security")
                .containsExactly(401, 403);
        assertThat(dashboardAuthWallScenario.expectedStatuses())
                .as("Spring Security may return 401 or 403 depending on filter order")
                .containsExactly(401, 403);
    }

    private boolean isSensitiveWriteScenario(ApiScenario scenario) {
        return WRITE_METHODS.contains(scenario.method())
                && (scenario.path().contains("tips")
                || scenario.path().contains("purchase")
                || scenario.path().contains("boost")
                || scenario.path().contains("top-ups"));
    }

    private ApiScenario findScenario(String id) {
        return SCENARIOS.stream()
                .filter(scenario -> scenario.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Missing scenario id: " + id));
    }
}