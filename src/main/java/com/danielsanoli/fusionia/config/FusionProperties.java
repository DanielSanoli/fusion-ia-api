package com.danielsanoli.fusionia.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fusion")
public record FusionProperties(
        String provider,
        String imageDir,
        int rateLimitPerHour
) {
    public FusionProperties {
        if (provider == null || provider.isBlank()) {
            provider = "stub";
        }
        if (imageDir == null || imageDir.isBlank()) {
            imageDir = "app/data/images";
        }
        if (rateLimitPerHour <= 0) {
            rateLimitPerHour = 10;
        }
    }
}

