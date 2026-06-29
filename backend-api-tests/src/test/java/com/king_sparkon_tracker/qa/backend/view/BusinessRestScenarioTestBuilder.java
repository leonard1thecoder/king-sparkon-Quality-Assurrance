package com.king_sparkon_tracker.qa.backend.view;

import com.king_sparkon_tracker.qa.backend.model.BusinessRestScenario;
import com.king_sparkon_tracker.qa.core.model.TestCaseModel;
import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestStatus;
import com.king_sparkon_tracker.qa.core.view.TestBuilder;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import static io.restassured.RestAssured.given;

public class BusinessRestScenarioTestBuilder implements TestBuilder {

    private final BusinessRestScenario scenario;
    private final String baseUrl;
    private final String authToken;

    public BusinessRestScenarioTestBuilder(BusinessRestScenario scenario, String baseUrl, String authToken) {
        this.scenario = scenario;
        this.baseUrl = baseUrl == null || baseUrl.isBlank() ? "http://localhost:8080" : baseUrl;
        this.authToken = authToken == null ? "" : authToken.trim();
    }

    @Override
    public TestExecutionResponse buildTest() {
        Instant startedAt = Instant.now();
        TestCaseModel model = new TestCaseModel(
                scenario.id(),
                scenario.domain() + " " + scenario.caseType(),
                scenario.name(),
                scenario.method() + " " + scenario.path()
        );

        if (scenario.requiresAuth() && authToken.isBlank()) {
            return new TestExecutionResponse(
                    scenario.id(),
                    scenario.domain() + " " + scenario.caseType(),
                    scenario.name(),
                    "Skipped because this scenario requires api.authToken.",
                    TestStatus.SKIPPED,
                    durationMs(startedAt),
                    "",
                    Instant.now()
            );
        }

        try {
            RestAssured.baseURI = baseUrl;
            RequestSpecification request = given()
                    .relaxedHTTPSValidation()
                    .accept("application/json")
                    .contentType("application/json");

            if (scenario.requiresAuth()) {
                request.header("Authorization", "Bearer " + authToken);
            }
            if (!scenario.body().isBlank()) {
                request.body(scenario.body());
            }

            Response response = execute(request);
            int actualStatus = response.statusCode();

            if (!statusMatches(actualStatus, scenario.expectedStatus())) {
                throw new AssertionError("Expected HTTP " + scenario.expectedStatus() + " but got " + actualStatus
                        + " for " + scenario.method() + " " + scenario.path()
                        + ". Response: " + response.asString());
            }

            if (!scenario.expectedBodyContains().isBlank() && !response.asString().contains(scenario.expectedBodyContains())) {
                throw new AssertionError("Expected response body to contain '" + scenario.expectedBodyContains() + "' but body was: " + response.asString());
            }

            return TestExecutionResponse.passed(
                    model,
                    durationMs(startedAt),
                    "REST scenario passed with HTTP " + actualStatus + "."
            );
        } catch (Throwable throwable) {
            return TestExecutionResponse.failed(
                    model,
                    durationMs(startedAt),
                    "REST scenario failed.",
                    throwable
            );
        }
    }

    private Response execute(RequestSpecification request) {
        return switch (scenario.method()) {
            case "GET" -> request.get(scenario.path());
            case "POST" -> request.post(scenario.path());
            case "PUT" -> request.put(scenario.path());
            case "PATCH" -> request.patch(scenario.path());
            case "DELETE" -> request.delete(scenario.path());
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + scenario.method());
        };
    }

    private boolean statusMatches(int actualStatus, String expectedStatus) {
        return Arrays.stream(expectedStatus.split("[,/ ]+"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .mapToInt(Integer::parseInt)
                .anyMatch(expected -> expected == actualStatus);
    }

    private long durationMs(Instant startedAt) {
        return Math.max(1, Duration.between(startedAt, Instant.now()).toMillis());
    }
}
