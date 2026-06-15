package com.danielsanoli.fusionia.provider;

import java.util.UUID;

public interface ImageGenerationProvider {

    ImageGenerationResult generate(UUID fusionId, String prompt);

    String providerName();
}

