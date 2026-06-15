package com.danielsanoli.fusionia.repository;

import com.danielsanoli.fusionia.domain.model.Fusion;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryFusionRepository implements FusionRepository {

    private final ConcurrentMap<UUID, Fusion> storage = new ConcurrentHashMap<>();

    @Override
    public Fusion save(Fusion fusion) {
        storage.put(fusion.id(), fusion);
        return fusion;
    }

    @Override
    public Optional<Fusion> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Fusion> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Fusion::createdAt).reversed())
                .toList();
    }
}

