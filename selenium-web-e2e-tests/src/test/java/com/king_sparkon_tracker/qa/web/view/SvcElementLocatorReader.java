package com.king_sparkon_tracker.qa.web.view;

import com.king_sparkon_tracker.qa.core.model.locator.ElementLocatorModel;
import com.king_sparkon_tracker.qa.core.model.locator.LocatorStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class SvcElementLocatorReader {

    private SvcElementLocatorReader() {
    }

    public static List<ElementLocatorModel> read(String resourcePath) {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalArgumentException("Locator file not found on classpath: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return reader.lines()
                    .skip(1)
                    .map(String::trim)
                    .filter(line -> !line.isBlank())
                    .filter(line -> !line.startsWith("#"))
                    .map(SvcElementLocatorReader::toLocator)
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read locator file: " + resourcePath, exception);
        }
    }

    private static ElementLocatorModel toLocator(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid locator line. Expected 6 columns but got " + parts.length + ": " + line);
        }
        return new ElementLocatorModel(
                required(parts[0], "id"),
                required(parts[1], "pagePath"),
                LocatorStrategy.from(required(parts[2], "strategy")),
                required(parts[3], "locatorValue"),
                parts[4],
                parts[5]
        );
    }

    private static String required(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required locator field: " + fieldName);
        }
        return value.trim();
    }
}
