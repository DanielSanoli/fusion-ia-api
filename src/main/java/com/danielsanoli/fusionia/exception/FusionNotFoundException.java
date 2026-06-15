package com.danielsanoli.fusionia.exception;

import java.util.UUID;

public class FusionNotFoundException extends RuntimeException {

    public FusionNotFoundException(UUID id) {
        super("Fusion not found: " + id);
    }
}

