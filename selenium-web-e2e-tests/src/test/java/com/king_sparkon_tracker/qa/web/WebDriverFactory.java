package com.king_sparkon_tracker.qa.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Locale;

public final class WebDriverFactory {

    private WebDriverFactory() {
    }

    public static WebDriver create() {
        String browser = System.getProperty("ui.browser", "chrome").toLowerCase(Locale.ROOT);
        String remoteUrl = System.getProperty("selenium.remoteUrl", "").trim();
        boolean headless = Boolean.parseBoolean(System.getProperty("ui.headless", "true"));

        WebDriver driver = remoteUrl.isBlank()
                ? createLocal(browser, headless)
                : createRemote(browser, headless, remoteUrl);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().window().maximize();
        return driver;
    }

    private static WebDriver createLocal(String browser, boolean headless) {
        return switch (browser) {
            case "firefox" -> new org.openqa.selenium.firefox.FirefoxDriver(firefoxOptions(headless));
            case "edge" -> new org.openqa.selenium.edge.EdgeDriver(edgeOptions(headless));
            case "chrome" -> new org.openqa.selenium.chrome.ChromeDriver(chromeOptions(headless));
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser + ". Use chrome, firefox, or edge.");
        };
    }

    private static WebDriver createRemote(String browser, boolean headless, String remoteUrl) {
        try {
            URL url = URI.create(remoteUrl).toURL();
            return switch (browser) {
                case "firefox" -> new RemoteWebDriver(url, firefoxOptions(headless));
                case "edge" -> new RemoteWebDriver(url, edgeOptions(headless));
                case "chrome" -> new RemoteWebDriver(url, chromeOptions(headless));
                default -> throw new IllegalArgumentException("Unsupported browser: " + browser + ". Use chrome, firefox, or edge.");
            };
        } catch (MalformedURLException exception) {
            throw new IllegalArgumentException("Invalid selenium.remoteUrl: " + remoteUrl, exception);
        }
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,1000", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1440,1000", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
        return options;
    }
}
