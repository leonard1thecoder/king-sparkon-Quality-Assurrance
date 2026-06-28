package com.king_sparkon_tracker.qa.core.view;

import com.king_sparkon_tracker.qa.core.model.TestSuiteReport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReportFileWriter {

    private final HtmlReportView htmlReportView;

    public ReportFileWriter() {
        this(new HtmlReportView());
    }

    public ReportFileWriter(HtmlReportView htmlReportView) {
        this.htmlReportView = htmlReportView;
    }

    public Path write(TestSuiteReport report) {
        return write(report, Path.of("target", "qa-report", "index.html"));
    }

    public Path write(TestSuiteReport report, Path outputPath) {
        try {
            Files.createDirectories(outputPath.getParent());
            Files.writeString(outputPath, htmlReportView.buildHtml(report), StandardCharsets.UTF_8);
            return outputPath;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to write QA HTML report to " + outputPath, exception);
        }
    }
}
