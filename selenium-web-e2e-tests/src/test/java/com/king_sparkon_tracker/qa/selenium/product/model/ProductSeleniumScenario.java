package com.king_sparkon_tracker.qa.selenium.product.model;

public record ProductSeleniumScenario(
        String id,
        String name,
        String path,
        String actionType,
        String locatorStrategy,
        String locatorValue,
        String inputValue,
        String expectedText,
        String priority,
        String description
) {
    public ProductSeleniumScenario {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Product Selenium scenario id is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product Selenium scenario name is required");
        }
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Product Selenium scenario path is required");
        }
        actionType = actionType == null || actionType.isBlank() ? "ASSERT_TEXT" : actionType;
        locatorStrategy = locatorStrategy == null || locatorStrategy.isBlank() ? "tag-name" : locatorStrategy;
        locatorValue = locatorValue == null || locatorValue.isBlank() ? "body" : locatorValue;
        inputValue = inputValue == null ? "" : inputValue;
        expectedText = expectedText == null ? "" : expectedText;
        priority = priority == null || priority.isBlank() ? "P2" : priority;
        description = description == null ? "" : description;
    }
}
