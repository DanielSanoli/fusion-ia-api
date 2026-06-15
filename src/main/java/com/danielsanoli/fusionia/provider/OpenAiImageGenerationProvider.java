package com.danielsanoli.fusionia.provider;

import com.danielsanoli.fusionia.domain.model.FusionStatus;
import com.danielsanoli.fusionia.exception.ImageGenerationException;
import com.danielsanoli.fusionia.exception.MissingProviderConfigurationException;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
@ConditionalOnProperty(prefix = "fusion", name = "provider", havingValue = "openai")
public class OpenAiImageGenerationProvider implements ImageGenerationProvider {

    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/png";

    private final ObjectProvider<ImageModel> imageModelProvider;
    private final String apiKey;
    private final String model;
    private final String size;
    private final String quality;
    private final String style;
    private final String responseFormat;

    public OpenAiImageGenerationProvider(ObjectProvider<ImageModel> imageModelProvider,
                                         @Value("${spring.ai.openai.api-key:}") String apiKey,
                                         @Value("${spring.ai.openai.image.options.model:gpt-image-1}") String model,
                                         @Value("${spring.ai.openai.image.options.size:1024x1024}") String size,
                                         @Value("${spring.ai.openai.image.options.quality:}") String quality,
                                         @Value("${spring.ai.openai.image.options.style:}") String style,
                                         @Value("${spring.ai.openai.image.options.response-format:}") String responseFormat) {
        this.imageModelProvider = imageModelProvider;
        this.apiKey = apiKey;
        this.model = model;
        this.size = size;
        this.quality = quality;
        this.style = style;
        this.responseFormat = responseFormat;
    }

    @Override
    public ImageGenerationResult generate(UUID fusionId, String prompt) {
        validateConfiguration();

        try {
            ImageModel imageModel = imageModelProvider.getIfAvailable();
            if (imageModel == null) {
                throw new MissingProviderConfigurationException(
                        "Spring AI ImageModel bean is not available. Check Spring AI OpenAI configuration."
                );
            }

            ImageResponse response = imageModel.call(new ImagePrompt(prompt, buildOptions()));
            Image image = extractImage(response);
            String imageUrl = trimToNull(image.getUrl());
            String imageBase64 = trimToNull(image.getB64Json());

            if (!StringUtils.hasText(imageUrl) && !StringUtils.hasText(imageBase64)) {
                throw new ImageGenerationException("OpenAI returned an empty image response");
            }

            return new ImageGenerationResult(
                    providerName(),
                    FusionStatus.READY,
                    imageUrl,
                    imageBase64,
                    imageBase64 == null ? null : DEFAULT_IMAGE_CONTENT_TYPE,
                    prompt,
                    metadata(response, imageUrl, imageBase64)
            );
        } catch (MissingProviderConfigurationException | ImageGenerationException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new ImageGenerationException("Failed to generate image using provider openai", exception);
        }
    }

    @Override
    public String providerName() {
        return "openai";
    }

    private void validateConfiguration() {
        if (!StringUtils.hasText(apiKey)) {
            throw new MissingProviderConfigurationException(
                    "OPENAI_API_KEY is required when fusion.provider=openai"
            );
        }
    }

    private OpenAiImageOptions buildOptions() {
        OpenAiImageOptions.Builder builder = OpenAiImageOptions.builder()
                .model(model)
                .N(1);

        if (StringUtils.hasText(size)) {
            builder.width(width(size));
            builder.height(height(size));
        }
        if (StringUtils.hasText(quality)) {
            builder.quality(quality);
        }
        if (StringUtils.hasText(style)) {
            builder.style(style);
        }
        if (StringUtils.hasText(responseFormat)) {
            builder.responseFormat(responseFormat);
        }

        return builder.build();
    }

    private Image extractImage(ImageResponse response) {
        if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
            throw new ImageGenerationException("OpenAI returned no image results");
        }

        ImageGeneration generation = response.getResult();
        if (generation == null || generation.getOutput() == null) {
            throw new ImageGenerationException("OpenAI returned an invalid image result");
        }

        return generation.getOutput();
    }

    private Map<String, Object> metadata(ImageResponse response, String imageUrl, String imageBase64) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("model", model);
        metadata.put("size", size);
        metadata.put("hasImageUrl", StringUtils.hasText(imageUrl));
        metadata.put("hasImageBase64", StringUtils.hasText(imageBase64));
        metadata.put("resultCount", response.getResults() == null ? 0 : response.getResults().size());
        if (response.getMetadata() != null) {
            metadata.put("responseMetadata", response.getMetadata().toString());
        }
        return metadata;
    }

    private int width(String size) {
        return parseSize(size)[0];
    }

    private int height(String size) {
        return parseSize(size)[1];
    }

    private int[] parseSize(String value) {
        String[] parts = value.toLowerCase().split("x");
        if (parts.length != 2) {
            throw new MissingProviderConfigurationException(
                    "spring.ai.openai.image.options.size must use WIDTHxHEIGHT format, for example 1024x1024"
            );
        }
        try {
            return new int[]{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())};
        } catch (NumberFormatException exception) {
            throw new MissingProviderConfigurationException(
                    "spring.ai.openai.image.options.size must contain numeric width and height"
            );
        }
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}


