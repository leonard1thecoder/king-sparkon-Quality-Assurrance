package com.king_sparkon_tracker.qa.core.view.storage;

import java.util.Optional;

public record SupabaseStorageConfig(
        String projectUrl,
        String bucket,
        String apiKey,
        boolean uploadEnabled,
        boolean upsert
) {
    public static SupabaseStorageConfig fromSystemProperties() {
        String projectUrl = firstNonBlank(
                System.getProperty("supabase.url"),
                System.getenv("SUPABASE_URL")
        );
        String bucket = firstNonBlank(
                System.getProperty("supabase.bucket"),
                System.getenv("SUPABASE_BUCKET")
        );
        String apiKey = firstNonBlank(
                System.getProperty("supabase.serviceRoleKey"),
                System.getProperty("supabase.anonKey"),
                System.getenv("SUPABASE_SERVICE_ROLE_KEY"),
                System.getenv("SUPABASE_ANON_KEY")
        );
        boolean enabled = Boolean.parseBoolean(firstNonBlank(
                System.getProperty("supabase.uploadEnabled"),
                System.getenv("SUPABASE_UPLOAD_ENABLED"),
                "false"
        ));
        boolean upsert = Boolean.parseBoolean(firstNonBlank(
                System.getProperty("supabase.upsert"),
                System.getenv("SUPABASE_UPSERT"),
                "true"
        ));
        return new SupabaseStorageConfig(projectUrl, bucket, apiKey, enabled, upsert);
    }

    public boolean ready() {
        return uploadEnabled
                && notBlank(projectUrl)
                && notBlank(bucket)
                && notBlank(apiKey);
    }

    public Optional<String> readinessIssue() {
        if (!uploadEnabled) {
            return Optional.of("Supabase upload disabled. Set -Dsupabase.uploadEnabled=true to upload screenshots.");
        }
        if (!notBlank(projectUrl)) {
            return Optional.of("Missing Supabase URL. Set -Dsupabase.url or SUPABASE_URL.");
        }
        if (!notBlank(bucket)) {
            return Optional.of("Missing Supabase bucket. Set -Dsupabase.bucket or SUPABASE_BUCKET.");
        }
        if (!notBlank(apiKey)) {
            return Optional.of("Missing Supabase API key. Set -Dsupabase.serviceRoleKey or SUPABASE_SERVICE_ROLE_KEY.");
        }
        return Optional.empty();
    }

    private static boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }
}
