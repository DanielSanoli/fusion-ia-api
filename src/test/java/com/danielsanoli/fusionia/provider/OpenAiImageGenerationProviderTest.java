package com.danielsanoli.fusionia.provider;

import com.danielsanoli.fusionia.exception.MissingProviderConfigurationException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.image.ImageModel;
import org.springframework.beans.factory.ObjectProvider;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class OpenAiImageGenerationProviderTest {

    @Test
    void shouldFailClearlyWhenApiKeyIsMissing() {
        @SuppressWarnings("unchecked")
        ObjectProvider<ImageModel> imageModelProvider = mock(ObjectProvider.class);

        OpenAiImageGenerationProvider provider = new OpenAiImageGenerationProvider(
                imageModelProvider,
                "",
                "gpt-image-1",
                "1024x1024",
                "",
                "",
                ""
        );

        assertThatThrownBy(() -> provider.generate(UUID.randomUUID(), "prompt"))
                .isInstanceOf(MissingProviderConfigurationException.class)
                .hasMessageContaining("OPENAI_API_KEY");
    }
}

