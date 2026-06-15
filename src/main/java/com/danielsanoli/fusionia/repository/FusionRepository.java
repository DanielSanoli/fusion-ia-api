package com.danielsanoli.fusionia.repository;

import com.danielsanoli.fusionia.domain.model.Fusion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FusionRepository {

    Fusion save(Fusion fusion);

    Optional<Fusion> findById(UUID id);

    List<Fusion> findAll();
}

