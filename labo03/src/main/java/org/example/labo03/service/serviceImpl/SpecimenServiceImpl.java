package org.example.labo03.service.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.labo03.common.mapper.SpecimenMapper;
import org.example.labo03.domain.dto.request.CreateSpecimenRequest;
import org.example.labo03.domain.dto.request.UpdateSpecimenRequest;
import org.example.labo03.domain.dto.response.SpecimenResponse;
import org.example.labo03.domain.entity.Specimen;
import org.example.labo03.exception.ResourceNotFoundException;
import org.example.labo03.repository.SpecimenRepository;
import org.example.labo03.service.SpecimenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpecimenServiceImpl implements SpecimenService {
    private final SpecimenRepository specimenRepository;
    private final SpecimenMapper specimenMapper;

    @Override
    @Transactional
    public SpecimenResponse createSpecimen(CreateSpecimenRequest request) {
        return specimenMapper.toDto(
                specimenRepository.save(specimenMapper.toEntityCreate(request))
        );
    }

    @Override
    public Page<SpecimenResponse> getAllSpecimens(Pageable pageable) {
        Page<Specimen> specimens = specimenRepository.findAll(pageable);

        if (specimens.isEmpty()) {
            throw new ResourceNotFoundException("No specimens are registered in Hyrule");
        }

        return specimenMapper.toDto(specimens);
    }

    @Override
    public SpecimenResponse getSpecimenById(UUID id) {
        return specimenMapper.toDto(specimenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specimen not found in Hyrule Records"))
        );
    }

    @Override
    @Transactional
    public SpecimenResponse updateSpecimen(UUID id, UpdateSpecimenRequest request) {
        this.getSpecimenById(id);
        return specimenMapper.toDto(specimenRepository.save(specimenMapper.toEntityUpdate(request, id)));
    }

    @Transactional
    public SpecimenResponse deleteSpecimen(UUID id) {
        SpecimenResponse existSpecimen = this.getSpecimenById(id);
        specimenRepository.deleteById(id);
        return existSpecimen;
    }
}