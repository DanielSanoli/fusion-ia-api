package com.danielsanoli.fusionia.dto;

import com.danielsanoli.fusionia.domain.model.FusionStatus;

import java.util.Map;
import java.util.UUID;

public record ImageResponse(
        UUID id,
        FusionStatus status,
        String provider,
        String imageUrl,
        String imageBase64,
        String imageContentType,
        Map<String, Object> metadata,
        String message
) {
}

