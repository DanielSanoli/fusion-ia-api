package com.danielsanoli.fusionia.provider;

import com.danielsanoli.fusionia.domain.model.FusionStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("spring-ai")
@ConditionalOnProperty(name = "fusion.provider", havingValue = "spring-ai")
public class SpringAiImageGenerationProvider implements ImageGenerationProvider {

    @Override
    public ImageGenerationResult generate(UUID fusionId, String prompt) {
        // TODO: Integrar com Spring AI ImageModel quando credenciais e provider real forem definidos.
        // A classe fica isolada por profile/propriedade para não exigir chave externa no desenvolvimento local.
        String imageUrl = "/api/v1/fusions/%s/image".formatted(fusionId);
        return new ImageGenerationResult(providerName(), FusionStatus.PENDING, imageUrl, prompt);
    }

    @Override
    public String providerName() {
        return "spring-ai";
    }
}

