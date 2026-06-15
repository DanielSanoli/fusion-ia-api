package com.danielsanoli.fusionia.service;

import com.danielsanoli.fusionia.domain.model.Fusion;
import com.danielsanoli.fusionia.domain.model.FusionStatus;
import com.danielsanoli.fusionia.dto.FusionRequest;
import com.danielsanoli.fusionia.exception.FusionNotFoundException;
import com.danielsanoli.fusionia.provider.StubImageGenerationProvider;
import com.danielsanoli.fusionia.repository.InMemoryFusionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FusionServiceTest {

    private FusionService fusionService;

    @BeforeEach
    void setUp() {
        fusionService = new FusionService(
                new PromptBuilderService(),
                new StubImageGenerationProvider(),
                new InMemoryFusionRepository()
        );
    }

    @Test
    void shouldCreateReadyFusionUsingStubProvider() {
        FusionRequest request = new FusionRequest(
                List.of(" Charizard ", "Gengar"),
                "dark fantasy sprite",
                "purple",
                123L
        );

        Fusion fusion = fusionService.create(request);

        assertThat(fusion.id()).isNotNull();
        assertThat(fusion.pokemons()).containsExactly("charizard", "gengar");
        assertThat(fusion.status()).isEqualTo(FusionStatus.READY);
        assertThat(fusion.provider()).isEqualTo("stub");
        assertThat(fusion.imageUrl()).isEqualTo("/api/v1/fusions/%s/image".formatted(fusion.id()));
        assertThat(fusion.prompt()).contains("Charizard and Gengar");
    }

    @Test
    void shouldFindCreatedFusionById() {
        Fusion fusion = fusionService.create(new FusionRequest(List.of("mew", "lucario"), null, null, null));

        assertThat(fusionService.findById(fusion.id())).isEqualTo(fusion);
    }

    @Test
    void shouldThrowWhenFusionDoesNotExist() {
        UUID id = UUID.randomUUID();

        assertThatThrownBy(() -> fusionService.findById(id))
                .isInstanceOf(FusionNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}

