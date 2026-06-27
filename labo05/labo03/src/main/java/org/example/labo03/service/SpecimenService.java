package org.example.labo03.service;

import org.example.labo03.domain.dto.request.CreateSpecimenRequest;
import org.example.labo03.domain.dto.request.UpdateSpecimenRequest;
import org.example.labo03.domain.dto.response.SpecimenResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface SpecimenService {
    SpecimenResponse createSpecimen(CreateSpecimenRequest request);

    Page<SpecimenResponse> getAllSpecimens(Pageable pageable);

    SpecimenResponse getSpecimenById(UUID id);

    SpecimenResponse updateSpecimen(UUID id, UpdateSpecimenRequest request);

    SpecimenResponse deleteSpecimen(UUID id);
}
