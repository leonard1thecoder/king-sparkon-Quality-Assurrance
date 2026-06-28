package com.king_sparkon_tracker.qa.web.view;

import com.king_sparkon_tracker.qa.core.model.locator.ElementLocatorModel;
import com.king_sparkon_tracker.qa.core.model.locator.LocatorStrategy;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SeleniumLocatorResolver {

    public WebElement find(WebDriver driver, ElementLocatorModel locator) {
        if (locator.strategy() == LocatorStrategy.JS_PATH) {
            Object result = ((JavascriptExecutor) driver).executeScript(locator.locatorValue());
            if (result instanceof WebElement webElement) {
                return webElement;
            }
            throw new IllegalArgumentException("JS path did not return a WebElement for locator: " + locator.id());
        }
        return driver.findElement(toBy(locator));
    }

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
            case JS_PATH -> throw new IllegalArgumentException("JS path must be resolved through JavascriptExecutor");
            case ACCESSIBILITY_ID, ANDROID_UI_AUTOMATOR, IOS_PREDICATE, IOS_CLASS_CHAIN ->
                    throw new IllegalArgumentException("Locator strategy " + locator.strategy() + " is Appium-only, not Selenium WebDriver");
        };
    }
}
