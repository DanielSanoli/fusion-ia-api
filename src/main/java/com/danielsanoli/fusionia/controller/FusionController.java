package com.danielsanoli.fusionia.controller;

import com.danielsanoli.fusionia.domain.model.Fusion;
import com.danielsanoli.fusionia.dto.FusionRequest;
import com.danielsanoli.fusionia.dto.FusionResponse;
import com.danielsanoli.fusionia.dto.ImageResponse;
import com.danielsanoli.fusionia.service.FusionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fusions")
public class FusionController {

    private final FusionService fusionService;

    public FusionController(FusionService fusionService) {
        this.fusionService = fusionService;
    }

    @PostMapping
    public ResponseEntity<FusionResponse> create(@Valid @RequestBody FusionRequest request) {
        Fusion fusion = fusionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(FusionResponse.from(fusion));
    }

    @GetMapping("/{id}")
    public FusionResponse findById(@PathVariable UUID id) {
        return FusionResponse.from(fusionService.findById(id));
    }

    @GetMapping
    public List<FusionResponse> findAll() {
        return fusionService.findAll().stream()
                .map(FusionResponse::from)
                .toList();
    }

    @GetMapping("/{id}/image")
    public ImageResponse getImage(@PathVariable UUID id) {
        Fusion fusion = fusionService.findById(id);
        return new ImageResponse(
                fusion.id(),
                fusion.status(),
                fusion.provider(),
                fusion.imageUrl(),
                "Image generation is currently served by the stub provider. No binary image is generated yet."
        );
    }
}

