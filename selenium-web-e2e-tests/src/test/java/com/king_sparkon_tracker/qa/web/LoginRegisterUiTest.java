package com.king_sparkon_tracker.qa.web;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRegisterUiTest {

    private static final String BASE_URL = System.getProperty("ui.baseUrl", "http://localhost:3000");
    private WebDriver driver;

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void loginPageShouldExposeUsableInputsAndRegisterLink() {
        driver = WebDriverFactory.create();
        driver.get(BASE_URL + "/login");

        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input")));

        List<WebElement> inputs = driver.findElements(By.cssSelector("input"));
        assertThat(inputs).as("Login page should have input fields").isNotEmpty();
        assertThat(driver.findElements(By.cssSelector("input[type='email'], input[name*='email'], input[placeholder*='Email'], input[placeholder*='email']")))
                .as("Login page should expose an email input")
                .isNotEmpty();
        assertThat(driver.findElements(By.cssSelector("input[type='password']")))
                .as("Login page should expose a password input")
                .isNotEmpty();
        assertThat(driver.findElements(By.cssSelector("a[href*='register'], a[href*='signup'], a[href*='sign-up']")))
                .as("Login page should link to registration")
                .isNotEmpty();
    }

    @Test
    void registerPageShouldExposeUsefulSignupInputsAndLoginLink() {
        driver = WebDriverFactory.create();
        driver.get(BASE_URL + "/register");

        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input")));

        assertThat(driver.findElements(By.cssSelector("input[type='email'], input[name*='email'], input[placeholder*='Email'], input[placeholder*='email']")))
                .as("Register page should expose an email input")
                .isNotEmpty();
        assertThat(driver.findElements(By.cssSelector("input[type='password']")))
                .as("Register page should expose password inputs")
                .isNotEmpty();
        assertThat(driver.findElements(By.cssSelector("a[href*='login'], a[href*='sign-in'], a[href*='signin']")))
                .as("Register page should link back to login")
                .isNotEmpty();
    }
}
