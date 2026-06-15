package com.danielsanoli.fusionia.dto;

import com.danielsanoli.fusionia.domain.model.Fusion;
import com.danielsanoli.fusionia.domain.model.FusionStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record FusionResponse(
        UUID id,
        List<String> pokemons,
        FusionStatus status,
        String prompt,
        String imageUrl,
        String provider,
        String imageContentType,
        Map<String, Object> metadata,
        String style,
        String dominantColor,
        Long seed,
        Instant createdAt
) {
    public static FusionResponse from(Fusion fusion) {
        return new FusionResponse(
                fusion.id(),
                fusion.pokemons(),
                fusion.status(),
                fusion.prompt(),
                fusion.imageUrl(),
                fusion.provider(),
                fusion.imageContentType(),
                fusion.metadata(),
                fusion.style(),
                fusion.dominantColor(),
                fusion.seed(),
                fusion.createdAt()
        );
    }
}

