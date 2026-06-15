package com.danielsanoli.fusionia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record FusionRequest(
        @NotNull(message = "pokemons is required")
        @Size(min = 2, max = 4, message = "pokemons must contain between 2 and 4 items")
        List<@NotBlank(message = "pokemon name must not be blank") String> pokemons,

        String style,
        String dominantColor,
        Long seed
) {
}

