package com.king_sparkon_tracker.qa.core.view.storage;

import com.king_sparkon_tracker.qa.core.model.screenshot.ScreenshotArtifact;
import com.king_sparkon_tracker.qa.core.model.screenshot.ScreenshotUploadResult;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class SupabaseScreenshotUploader {

    private final HttpClient httpClient;
    private final SupabaseStorageConfig config;

    public SupabaseScreenshotUploader() {
        this(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build(), SupabaseStorageConfig.fromSystemProperties());
    }

    public SupabaseScreenshotUploader(HttpClient httpClient, SupabaseStorageConfig config) {
        this.httpClient = httpClient;
        this.config = config;
    }

    public ScreenshotUploadResult upload(ScreenshotArtifact artifact) {
        if (!config.ready()) {
            return ScreenshotUploadResult.localOnly(artifact.localPath(), config.readinessIssue().orElse("Supabase upload is not ready"));
        }

        String remotePath = "qa-screenshots/" + artifact.testCaseId().replaceAll("[^a-zA-Z0-9._-]", "-") + "/" + artifact.fileName();
        URI uploadUri = URI.create(trimSlash(config.projectUrl()) + "/storage/v1/object/" + encode(config.bucket()) + "/" + encodePath(remotePath));

        HttpRequest request = HttpRequest.newBuilder(uploadUri)
                .timeout(Duration.ofSeconds(60))
                .header("apikey", config.apiKey())
                .header("Authorization", "Bearer " + config.apiKey())
                .header("Content-Type", artifact.contentType())
                .header("x-upsert", Boolean.toString(config.upsert()))
                .POST(HttpRequest.BodyPublishers.ofByteArray(artifact.bytes()))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return ScreenshotUploadResult.uploaded(artifact.localPath(), remotePath, publicUrl(remotePath));
            }
            return ScreenshotUploadResult.localOnly(
                    artifact.localPath(),
                    "Supabase upload failed with HTTP " + response.statusCode() + ": " + response.body()
            );
        } catch (IOException exception) {
            return ScreenshotUploadResult.localOnly(artifact.localPath(), "Supabase upload failed: " + exception.getMessage());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return ScreenshotUploadResult.localOnly(artifact.localPath(), "Supabase upload interrupted: " + exception.getMessage());
        }
    }

    private String publicUrl(String remotePath) {
        return trimSlash(config.projectUrl()) + "/storage/v1/object/public/" + encode(config.bucket()) + "/" + encodePath(remotePath);
    }

    private String trimSlash(String value) {
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private String encodePath(String path) {
        String[] parts = path.split("/");
        StringBuilder encoded = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                encoded.append('/');
            }
            encoded.append(encode(parts[i]));
        }
        return encoded.toString();
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
