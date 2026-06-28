package com.king_sparkon_tracker.qa.testproduct.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ProductScenarioCatalog {

    public List<ProductTestCaseDefinition> loadTestCases(String classpathResource) {
        return readLines(classpathResource).stream()
                .skip(1)
                .filter(line -> !line.isBlank())
                .map(line -> line.split("\\|", -1))
                .map(columns -> {
                    requireColumns(classpathResource, columns, 8);
                    return new ProductTestCaseDefinition(
                            columns[0],
                            columns[1],
                            columns[2],
                            columns[3],
                            columns[4],
                            columns[5],
                            columns[6],
                            columns[7]
                    );
                })
                .toList();
    }

    public List<ProductTestStep> loadSteps(String classpathResource) {
        return readLines(classpathResource).stream()
                .skip(1)
                .filter(line -> !line.isBlank())
                .map(line -> line.split("\\|", -1))
                .map(columns -> {
                    requireColumns(classpathResource, columns, 8);
                    return new ProductTestStep(
                            columns[0],
                            columns[1],
                            Integer.parseInt(columns[2]),
                            columns[3],
                            columns[4],
                            columns[5],
                            columns[6],
                            columns[7]
                    );
                })
                .toList();
    }

    private List<String> readLines(String classpathResource) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream stream = classLoader.getResourceAsStream(classpathResource)) {
            if (stream == null) {
                throw new IllegalArgumentException("Missing QA resource: " + classpathResource);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    String cleanLine = line.trim();
                    if (!cleanLine.isBlank() && !cleanLine.startsWith("#")) {
                        lines.add(cleanLine);
                    }
                }
                return lines;
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read QA resource: " + classpathResource, exception);
        }
    }

    private static void requireColumns(String resource, String[] columns, int expected) {
        if (columns.length < expected) {
            throw new IllegalArgumentException(resource + " expected at least " + expected + " columns but found " + columns.length);
        }
    }
}
