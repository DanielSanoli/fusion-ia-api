package com.danielsanoli.fusionia.provider;

import com.danielsanoli.fusionia.domain.model.FusionStatus;

public record ImageGenerationResult(
        String provider,
        FusionStatus status,
        String imageUrl,
        String prompt
) {
}

