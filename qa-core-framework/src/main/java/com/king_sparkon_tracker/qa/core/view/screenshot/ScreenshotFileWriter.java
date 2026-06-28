package com.king_sparkon_tracker.qa.core.view.screenshot;

import com.king_sparkon_tracker.qa.core.model.screenshot.ScreenshotArtifact;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScreenshotFileWriter {

    public ScreenshotArtifact write(String testCaseId, byte[] bytes) {
        String safeId = testCaseId.replaceAll("[^a-zA-Z0-9._-]", "-");
        String fileName = safeId + "-" + System.currentTimeMillis() + ".png";
        Path outputPath = Path.of("target", "qa-screenshots", fileName);
        try {
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, bytes);
            return new ScreenshotArtifact(testCaseId, fileName, "image/png", bytes, outputPath);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to write screenshot to " + outputPath, exception);
        }
    }
}
