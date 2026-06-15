package com.danielsanoli.fusionia.dto;

import com.danielsanoli.fusionia.domain.model.FusionStatus;

import java.util.UUID;

public record ImageResponse(
        UUID id,
        FusionStatus status,
        String provider,
        String imageUrl,
        String message
) {
}

