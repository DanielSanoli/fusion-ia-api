package com.danielsanoli.fusionia.service;

import com.danielsanoli.fusionia.domain.model.Fusion;
import com.danielsanoli.fusionia.dto.FusionRequest;
import com.danielsanoli.fusionia.exception.FusionNotFoundException;
import com.danielsanoli.fusionia.provider.ImageGenerationProvider;
import com.danielsanoli.fusionia.provider.ImageGenerationResult;
import com.danielsanoli.fusionia.repository.FusionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class FusionService {

    private final PromptBuilderService promptBuilderService;
    private final ImageGenerationProvider imageGenerationProvider;
    private final FusionRepository fusionRepository;

    public FusionService(PromptBuilderService promptBuilderService,
                         ImageGenerationProvider imageGenerationProvider,
                         FusionRepository fusionRepository) {
        this.promptBuilderService = promptBuilderService;
        this.imageGenerationProvider = imageGenerationProvider;
        this.fusionRepository = fusionRepository;
    }

    public Fusion create(FusionRequest request) {
        List<String> pokemons = promptBuilderService.normalizePokemons(request.pokemons());
        String prompt = promptBuilderService.buildPrompt(pokemons, request.style(), request.dominantColor(), request.seed());
        UUID fusionId = UUID.randomUUID();
        ImageGenerationResult generationResult = imageGenerationProvider.generate(fusionId, prompt);

        Fusion fusion = new Fusion(
                fusionId,
                pokemons,
                trimToNull(request.style()),
                trimToNull(request.dominantColor()),
                request.seed(),
                generationResult.status(),
                generationResult.prompt(),
                generationResult.imageUrl(),
                generationResult.imageBase64(),
                generationResult.imageContentType(),
                generationResult.provider(),
                generationResult.metadata(),
                Instant.now()
        );

        return fusionRepository.save(fusion);
    }

    public Fusion findById(UUID id) {
        return fusionRepository.findById(id)
                .orElseThrow(() -> new FusionNotFoundException(id));
    }

    public List<Fusion> findAll() {
        return fusionRepository.findAll();
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

