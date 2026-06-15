package com.danielsanoli.fusionia.service;

import com.danielsanoli.fusionia.exception.InvalidFusionRequestException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PromptBuilderServiceTest {

    private final PromptBuilderService promptBuilderService = new PromptBuilderService();

    @Test
    void shouldBuildPromptWithOptionalFields() {
        String prompt = promptBuilderService.buildPrompt(
                List.of("charizard", "gengar"),
                "dark fantasy sprite",
                "purple",
                123L
        );

        assertThat(prompt)
                .contains("Create a Pokémon-inspired fusion between Charizard and Gengar.")
                .contains("Style: dark fantasy sprite.")
                .contains("Dominant color: purple.")
                .contains("Seed: 123.")
                .contains("Keep the design consistent with monster game aesthetics.")
                .contains("Generate a clean character concept.");
    }

    @Test
    void shouldOmitOptionalFieldsWhenAbsent() {
        String prompt = promptBuilderService.buildPrompt(List.of("bulbasaur", "squirtle"), null, " ", null);

        assertThat(prompt)
                .contains("Bulbasaur and Squirtle")
                .doesNotContain("Style:")
                .doesNotContain("Dominant color:")
                .doesNotContain("Seed:");
    }

    @Test
    void shouldRejectInvalidPokemonList() {
        assertThatThrownBy(() -> promptBuilderService.buildPrompt(List.of("pikachu"), null, null, null))
                .isInstanceOf(InvalidFusionRequestException.class)
                .hasMessageContaining("between 2 and 4");
    }
}

