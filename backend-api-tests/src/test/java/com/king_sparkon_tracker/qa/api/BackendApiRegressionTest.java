package com.king_sparkon_tracker.qa.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BackendApiRegressionTest {

    private static final String SCENARIO_FILE = "scenarios/backend-api-test-cases.svc";
    private static String authToken;

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = System.getProperty("api.baseUrl", "http://localhost:8080");
        authToken = System.getProperty("api.authToken", "").trim();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    static Stream<ApiScenario> apiScenarios() {
        return SvcScenarioReader.readApiScenarios(SCENARIO_FILE).stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("apiScenarios")
    void backendApiScenarioShouldReturnExpectedStatus(ApiScenario scenario) {
        if (scenario.requiresAuth()) {
            Assumptions.assumeTrue(!authToken.isBlank(), "Skipping auth-required API scenario because api.authToken is empty: " + scenario.id());
        }

        Response response = execute(scenario);

        assertThat(response.statusCode())
                .as("%s %s should return HTTP %s. Body: %s", scenario.method(), scenario.path(), scenario.expectedStatus(), response.asPrettyString())
                .isEqualTo(scenario.expectedStatus());
    }

    private Response execute(ApiScenario scenario) {
        RequestSpecification request = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON);

        if (!authToken.isBlank()) {
            request.header("Authorization", "Bearer " + authToken);
        }

        if (!scenario.body().isBlank()) {
            request.body(scenario.body());
        }

        return switch (scenario.method()) {
            case "GET" -> request.get(scenario.path());
            case "POST" -> request.post(scenario.path());
            case "PUT" -> request.put(scenario.path());
            case "PATCH" -> request.patch(scenario.path());
            case "DELETE" -> request.delete(scenario.path());
            default -> throw new IllegalArgumentException("Unsupported HTTP method in SVC file: " + scenario.method());
        };
    }
}
