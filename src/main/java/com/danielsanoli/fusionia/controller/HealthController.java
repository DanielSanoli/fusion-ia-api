package com.danielsanoli.fusionia.controller;

import com.danielsanoli.fusionia.config.FusionProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private final FusionProperties fusionProperties;

    public HealthController(FusionProperties fusionProperties) {
        this.fusionProperties = fusionProperties;
    }

    @GetMapping
    public Map<String, Object> health() {
        return Map.of(
                "status", "ok",
                "service", "fusion-ia-api",
                "provider", fusionProperties.provider()
        );
    }
}

