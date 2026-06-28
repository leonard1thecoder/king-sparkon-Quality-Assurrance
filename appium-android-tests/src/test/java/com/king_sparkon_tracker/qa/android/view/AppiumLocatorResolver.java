package com.king_sparkon_tracker.qa.android.view;

import com.king_sparkon_tracker.qa.core.model.locator.ElementLocatorModel;
import com.king_sparkon_tracker.qa.core.model.locator.LocatorStrategy;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class AppiumLocatorResolver {

    public By toBy(ElementLocatorModel locator) {
        return switch (locator.strategy()) {
            case ID -> By.id(locator.locatorValue());
            case NAME -> By.name(locator.locatorValue());
            case CSS -> By.cssSelector(locator.locatorValue());
            case CLASS_NAME -> By.className(locator.locatorValue());
            case TAG_NAME -> By.tagName(locator.locatorValue());
            case LINK_TEXT -> By.linkText(locator.locatorValue());
            case PARTIAL_LINK_TEXT -> By.partialLinkText(locator.locatorValue());
            case XPATH, FULL_XPATH -> By.xpath(locator.locatorValue());
            case ACCESSIBILITY_ID -> AppiumBy.accessibilityId(locator.locatorValue());
            case ANDROID_UI_AUTOMATOR -> AppiumBy.androidUIAutomator(locator.locatorValue());
            case IOS_PREDICATE -> AppiumBy.iOSNsPredicateString(locator.locatorValue());
            case IOS_CLASS_CHAIN -> AppiumBy.iOSClassChain(locator.locatorValue());
            case JS_PATH -> throw new IllegalArgumentException("JS path is not supported for native Appium Android screens");
        };
    }
}
