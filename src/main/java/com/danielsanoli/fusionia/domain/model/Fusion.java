package com.danielsanoli.fusionia.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record Fusion(
        UUID id,
        List<String> pokemons,
        String style,
        String dominantColor,
        Long seed,
        FusionStatus status,
        String prompt,
        String imageUrl,
        String imageBase64,
        String imageContentType,
        String provider,
        Map<String, Object> metadata,
        Instant createdAt
) {
    public Fusion {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}

