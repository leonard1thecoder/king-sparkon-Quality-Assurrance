package com.king_sparkon_tracker.qa.core.model;

public record AutomationAnalysisModel(
        String featureId,
        String featureName,
        String testCaseId,
        String scenario,
        AutomationCandidate candidate,
        String reason,
        String automationLayer,
        String manualOwner,
        String prerequisite,
        String risk
) {
    public AutomationAnalysisModel {
        if (featureId == null || featureId.isBlank()) {
            throw new IllegalArgumentException("featureId is required");
        }
        if (featureName == null || featureName.isBlank()) {
            throw new IllegalArgumentException("featureName is required");
        }
        if (testCaseId == null || testCaseId.isBlank()) {
            throw new IllegalArgumentException("testCaseId is required");
        }
        if (scenario == null || scenario.isBlank()) {
            throw new IllegalArgumentException("scenario is required");
        }
        candidate = candidate == null ? AutomationCandidate.NOT_READY : candidate;
        reason = reason == null ? "" : reason.trim();
        automationLayer = automationLayer == null ? "" : automationLayer.trim();
        manualOwner = manualOwner == null ? "" : manualOwner.trim();
        prerequisite = prerequisite == null ? "" : prerequisite.trim();
        risk = risk == null ? "" : risk.trim();
    }
}
