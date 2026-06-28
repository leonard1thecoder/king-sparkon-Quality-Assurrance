package com.king_sparkon_tracker.qa.core.model.locator;

import java.util.Locale;

public enum LocatorStrategy {
    ID,
    NAME,
    CSS,
    CLASS_NAME,
    TAG_NAME,
    LINK_TEXT,
    PARTIAL_LINK_TEXT,
    XPATH,
    FULL_XPATH,
    JS_PATH,
    ACCESSIBILITY_ID,
    ANDROID_UI_AUTOMATOR,
    IOS_PREDICATE,
    IOS_CLASS_CHAIN;

    public static LocatorStrategy from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Locator strategy is required");
        }
        String normalized = value.trim()
                .replace("-", "_")
                .replace(" ", "_")
                .toUpperCase(Locale.ROOT);
        if ("FULLXPATH".equals(normalized) || "FULL_XPPTH".equals(normalized) || "FULL_XPATH".equals(normalized)) {
            return FULL_XPATH;
        }
        if ("JSPATH".equals(normalized) || "JS_PATH".equals(normalized)) {
            return JS_PATH;
        }
        if ("XPATH".equals(normalized) || "X_PATH".equals(normalized)) {
            return XPATH;
        }
        if ("ACCESSIBILITYID".equals(normalized) || "ACCESSIBILITY_ID".equals(normalized) || "A11Y".equals(normalized)) {
            return ACCESSIBILITY_ID;
        }
        return LocatorStrategy.valueOf(normalized);
    }
}
