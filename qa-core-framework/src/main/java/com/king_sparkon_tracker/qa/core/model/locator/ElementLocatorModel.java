package com.king_sparkon_tracker.qa.core.model.locator;

public record ElementLocatorModel(
        String id,
        String pagePath,
        LocatorStrategy strategy,
        String locatorValue,
        String expectedText,
        String description
) {
    public ElementLocatorModel {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Locator id is required");
        }
        pagePath = pagePath == null || pagePath.isBlank() ? "/" : pagePath.trim();
        if (strategy == null) {
            throw new IllegalArgumentException("Locator strategy is required");
        }
        if (locatorValue == null || locatorValue.isBlank()) {
            throw new IllegalArgumentException("Locator value is required");
        }
        expectedText = expectedText == null ? "" : expectedText.trim();
        description = description == null ? "" : description.trim();
    }
}
