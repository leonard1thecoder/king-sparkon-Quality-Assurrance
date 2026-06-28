package com.king_sparkon_tracker.qa.core.model.screenshot;

import java.nio.file.Path;

public record ScreenshotArtifact(
        String testCaseId,
        String fileName,
        String contentType,
        byte[] bytes,
        Path localPath
) {
    public ScreenshotArtifact {
        if (testCaseId == null || testCaseId.isBlank()) {
            throw new IllegalArgumentException("testCaseId is required");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("fileName is required");
        }
        contentType = contentType == null || contentType.isBlank() ? "image/png" : contentType;
        bytes = bytes == null ? new byte[0] : bytes.clone();
    }

    @Override
    public byte[] bytes() {
        return bytes.clone();
    }
}
