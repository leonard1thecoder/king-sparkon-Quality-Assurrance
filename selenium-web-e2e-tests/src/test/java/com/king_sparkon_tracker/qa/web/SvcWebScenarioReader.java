package com.king_sparkon_tracker.qa.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class SvcWebScenarioReader {

    private SvcWebScenarioReader() {
    }

    public static List<WebScenario> read(String resourcePath) {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalArgumentException("Scenario file not found on classpath: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return reader.lines()
                    .skip(1)
                    .map(String::trim)
                    .filter(line -> !line.isBlank())
                    .filter(line -> !line.startsWith("#"))
                    .map(SvcWebScenarioReader::toScenario)
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read scenario file: " + resourcePath, exception);
        }
    }

    private static WebScenario toScenario(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid web scenario line. Expected 5 columns but got " + parts.length + ": " + line);
        }

        return new WebScenario(
                required(parts[0], "id"),
                required(parts[1], "name"),
                required(parts[2], "path"),
                required(parts[3], "expectedContent"),
                Boolean.parseBoolean(required(parts[4], "requiresAuth"))
        );
    }

    private static String required(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required SVC field: " + fieldName);
        }
        return value.trim();
    }
}
