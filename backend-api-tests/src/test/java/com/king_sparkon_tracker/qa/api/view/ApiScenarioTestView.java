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
                "Expected HTTP " + scenario.expectedStatusesLabel()
        ));
        this.scenario = scenario;
        this.authToken = authToken == null ? "" : authToken.trim();
    }

    @Override
    protected List<TestStepModel> endToEndSteps() {
        return List.of(
                step(1, "Prepare REST Assured request for " + scenario.method() + " " + scenario.path(), "Request specification is ready"),
                step(2, "Attach token only when scenario requires authentication", "Protected positive scenarios can authenticate without changing public or negative scenarios"),
                step(3, "Attach request body when scenario defines one", "Request payload is ready"),
                step(4, "Execute backend API request", "Backend responds"),
                step(5, "Assert expected HTTP status " + scenario.expectedStatusesLabel(), "API scenario passes when status is accepted")
        );
    }

    @Override
    protected String executeTestLogic() {
        Response response = executeRequest();
        int actualStatus = response.statusCode();

        assertThat(scenario.acceptsStatus(actualStatus))
                .as("%s %s should return one of HTTP [%s] but returned HTTP %s. Body: %s",
                        scenario.method(),
                        scenario.path(),
                        scenario.expectedStatusesLabel(),
                        actualStatus,
                        response.asPrettyString())
                .isTrue();

        return scenario.method() + " " + scenario.path() + " returned accepted HTTP " + actualStatus;
    }

    @Override
    protected String failureDescription() {
        return scenario.method() + " " + scenario.path() + " did not return expected HTTP " + scenario.expectedStatusesLabel();
    }

    private Response executeRequest() {
        RequestSpecification request = RestAssured.given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .header("X-QA-Test-Run", "king-sparkon-preprod-contract");

        if (scenario.requiresAuth() && !authToken.isBlank()) {
            request.auth().oauth2(authToken);
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