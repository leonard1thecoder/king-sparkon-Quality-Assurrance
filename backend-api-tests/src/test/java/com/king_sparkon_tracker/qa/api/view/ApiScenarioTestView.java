package com.king_sparkon_tracker.qa.api.view;

import com.king_sparkon_tracker.qa.api.model.ApiScenario;
import com.king_sparkon_tracker.qa.core.model.TestCaseModel;
import com.king_sparkon_tracker.qa.core.model.TestStepModel;
import com.king_sparkon_tracker.qa.core.view.AbstractTestView;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiScenarioTestView extends AbstractTestView {

    private final ApiScenario scenario;
    private final String authToken;

    public ApiScenarioTestView(ApiScenario scenario, String authToken) {
        super(new TestCaseModel(
                scenario.id(),
                "Backend API - " + scenario.method() + " " + scenario.path(),
                scenario.name(),
                "Expected HTTP " + scenario.expectedStatus()
        ));
        this.scenario = scenario;
        this.authToken = authToken == null ? "" : authToken.trim();
    }

    @Override
    protected List<TestStepModel> endToEndSteps() {
        return List.of(
                step(1, "Prepare REST Assured request for " + scenario.method() + " " + scenario.path(), "Request specification is ready"),
                step(2, "Attach bearer token when scenario requires authentication", "Authorization header is available for protected APIs"),
                step(3, "Attach request body when scenario defines one", "Request payload is ready"),
                step(4, "Execute backend API request", "Backend responds"),
                step(5, "Assert expected HTTP status " + scenario.expectedStatus(), "API scenario passes when status matches expected value")
        );
    }

    @Override
    protected String executeTestLogic() {
        Response response = executeRequest();
        assertThat(response.statusCode())
                .as("%s %s should return HTTP %s. Body: %s", scenario.method(), scenario.path(), scenario.expectedStatus(), response.asPrettyString())
                .isEqualTo(scenario.expectedStatus());

        return scenario.method() + " " + scenario.path() + " returned expected HTTP " + scenario.expectedStatus();
    }

    @Override
    protected String failureDescription() {
        return scenario.method() + " " + scenario.path() + " did not return expected HTTP " + scenario.expectedStatus();
    }

    private Response executeRequest() {
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
