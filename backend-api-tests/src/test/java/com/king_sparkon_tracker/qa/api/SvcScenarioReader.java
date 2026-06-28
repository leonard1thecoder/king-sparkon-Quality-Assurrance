package com.king_sparkon_tracker.qa.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public final class SvcScenarioReader {

    private SvcScenarioReader() {
    }

    public static List<ApiScenario> readApiScenarios(String resourcePath) {
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
                    .map(SvcScenarioReader::toApiScenario)
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read scenario file: " + resourcePath, exception);
        }
    }

    private static ApiScenario toApiScenario(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 7) {
            throw new IllegalArgumentException("Invalid API scenario line. Expected 7 columns but got " + parts.length + ": " + line);
        }

        return new ApiScenario(
                required(parts[0], "id"),
                required(parts[1], "name"),
                required(parts[2], "method").toUpperCase(),
                required(parts[3], "path"),
                Objects.toString(parts[4], ""),
                Integer.parseInt(required(parts[5], "expectedStatus")),
                Boolean.parseBoolean(required(parts[6], "requiresAuth"))
        );
    }

    private static String required(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required SVC field: " + fieldName);
        }
        return value.trim();
    }
}
