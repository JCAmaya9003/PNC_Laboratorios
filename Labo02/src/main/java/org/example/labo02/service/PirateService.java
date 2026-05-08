package org.example.labo02.service;

import org.example.labo02.domain.entity.Pirate;

import java.util.List;
import java.util.UUID;

public interface PirateService {
    Pirate getPirateById(UUID id);
    List<Pirate> getPirates();
    void createPirate(Pirate pirate);
    void editPirate(UUID id, Pirate pirate);
    Pirate deletePirate(UUID id);
}