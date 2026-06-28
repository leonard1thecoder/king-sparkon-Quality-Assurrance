package com.king_sparkon_tracker.qa.core.model.screenshot;

import java.nio.file.Path;

public record ScreenshotUploadResult(
        boolean uploaded,
        Path localPath,
        String remotePath,
        String publicUrl,
        String message
) {
    public static ScreenshotUploadResult localOnly(Path localPath, String message) {
        return new ScreenshotUploadResult(false, localPath, "", "", message);
    }

    public static ScreenshotUploadResult uploaded(Path localPath, String remotePath, String publicUrl) {
        return new ScreenshotUploadResult(true, localPath, remotePath, publicUrl, "Uploaded to Supabase Storage");
    }
}
