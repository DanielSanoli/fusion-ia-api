package com.danielsanoli.fusionia.provider;

import com.danielsanoli.fusionia.domain.model.FusionStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "fusion.provider", havingValue = "stub", matchIfMissing = true)
public class StubImageGenerationProvider implements ImageGenerationProvider {

    @Override
    public ImageGenerationResult generate(UUID fusionId, String prompt) {
        String imageUrl = "/api/v1/fusions/%s/image".formatted(fusionId);
        return new ImageGenerationResult(providerName(), FusionStatus.READY, imageUrl, prompt);
    }

    @Override
    public String providerName() {
        return "stub";
    }
}

