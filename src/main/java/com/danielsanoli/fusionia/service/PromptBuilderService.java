package com.danielsanoli.fusionia.service;

import com.danielsanoli.fusionia.exception.InvalidFusionRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class PromptBuilderService {

    public String buildPrompt(List<String> pokemons, String style, String dominantColor, Long seed) {
        List<String> normalizedPokemons = normalizePokemons(pokemons);

        StringBuilder prompt = new StringBuilder();
        prompt.append("Create a Pokémon-inspired fusion between ")
                .append(joinPokemonNames(normalizedPokemons))
                .append(".\n");

        if (hasText(style)) {
            prompt.append("Style: ").append(style.trim()).append(".\n");
        }
        if (hasText(dominantColor)) {
            prompt.append("Dominant color: ").append(dominantColor.trim()).append(".\n");
        }
        if (seed != null) {
            prompt.append("Seed: ").append(seed).append(".\n");
        }

        prompt.append("Keep the design consistent with monster game aesthetics.\n")
                .append("Generate a clean character concept.");

        return prompt.toString();
    }

    public List<String> normalizePokemons(List<String> pokemons) {
        if (pokemons == null || pokemons.isEmpty()) {
            throw new InvalidFusionRequestException("pokemons must contain between 2 and 4 items");
        }

        List<String> normalized = pokemons.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(this::hasText)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .toList();

        if (normalized.size() < 2 || normalized.size() > 4) {
            throw new InvalidFusionRequestException("pokemons must contain between 2 and 4 valid names");
        }

        return normalized;
    }

    private String joinPokemonNames(List<String> pokemons) {
        List<String> displayNames = pokemons.stream()
                .map(this::toDisplayName)
                .toList();

        if (displayNames.size() == 2) {
            return displayNames.get(0) + " and " + displayNames.get(1);
        }

        String allButLast = String.join(", ", displayNames.subList(0, displayNames.size() - 1));
        return allButLast + " and " + displayNames.get(displayNames.size() - 1);
    }

    private String toDisplayName(String value) {
        String[] parts = value.split("[-\\s_]+");
        StringBuilder display = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (!display.isEmpty()) {
                display.append(' ');
            }
            display.append(part.substring(0, 1).toUpperCase(Locale.ROOT));
            if (part.length() > 1) {
                display.append(part.substring(1).toLowerCase(Locale.ROOT));
            }
        }
        return display.toString();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

