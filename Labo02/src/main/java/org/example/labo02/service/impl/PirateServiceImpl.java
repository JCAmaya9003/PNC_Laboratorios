package org.example.labo02.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.labo02.domain.entity.Pirate;
import org.example.labo02.repository.PirateRepository;
import org.example.labo02.service.PirateService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PirateServiceImpl implements PirateService{
    private final PirateRepository pirateRepository;

    @Override
    public Pirate getPirateById(UUID id) {
        return pirateRepository.getReferenceById(id);
    }

    @Override
    public List<Pirate> getPirates() {
        return pirateRepository.findAll();
    }

    @Override
    public void createPirate(Pirate pirate) {
        pirateRepository.save(pirate);
    }
    @Override
    public void editPirate(UUID id, Pirate pirate) {
        Pirate existPirate = pirateRepository.getReferenceById(id);
        existPirate.setName(pirate.getName());
        existPirate.setBounty(pirate.getBounty());
        existPirate.setCrew(pirate.getCrew());
        existPirate.setIsALive(pirate.getIsALive());
        pirateRepository.save(existPirate);
    }
    @Override
    public Pirate deletePirate(UUID id) {
        Pirate existPirate = pirateRepository.getReferenceById(id);
        pirateRepository.deleteById(id);
        return existPirate;
    }
}
