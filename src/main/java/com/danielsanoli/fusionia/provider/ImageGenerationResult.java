package com.danielsanoli.fusionia.provider;

import com.danielsanoli.fusionia.domain.model.FusionStatus;

import java.util.Map;

public record ImageGenerationResult(
        String provider,
        FusionStatus status,
        String imageUrl,
        String imageBase64,
        String imageContentType,
        String prompt,
        Map<String, Object> metadata
) {

    public ImageGenerationResult {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}

