package com.danielsanoli.fusionia.domain.model;

import java.time.Instant;
import java.util.List;
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
        String provider,
        Instant createdAt
) {
}

