package com.king_sparkon_tracker.qa.selenium.product.view;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.Locale;

public class SeleniumDriverFactory {

    public WebDriver create() {
        String browser = System.getProperty("ui.browser", "chrome").toLowerCase(Locale.ROOT);
        String remoteUrl = System.getProperty("selenium.remoteUrl", "");
        boolean headless = Boolean.parseBoolean(System.getProperty("ui.headless", "true"));

        if (!"chrome".equals(browser)) {
            throw new IllegalArgumentException("Only chrome is currently supported by King Sparkon Selenium QA. Requested: " + browser);
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1440,1100");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        if (headless) {
            options.addArguments("--headless=new");
        }

        WebDriver driver = remoteUrl == null || remoteUrl.isBlank()
                ? new ChromeDriver(options)
                : createRemoteDriver(remoteUrl, options);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        return driver;
    }

    private WebDriver createRemoteDriver(String remoteUrl, ChromeOptions options) {
        try {
            return RemoteWebDriver.builder()
                    .oneOf(options)
                    .address(URI.create(remoteUrl).toURL())
                    .build();
        } catch (MalformedURLException exception) {
            throw new IllegalArgumentException("Invalid selenium.remoteUrl: " + remoteUrl, exception);
        }
    }
}
