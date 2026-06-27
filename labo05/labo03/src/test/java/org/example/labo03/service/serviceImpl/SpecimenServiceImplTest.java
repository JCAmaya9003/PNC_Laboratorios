package org.example.labo03.service.serviceImpl;

import org.example.labo03.common.mapper.SpecimenMapper;
import org.example.labo03.domain.dto.request.CreateSpecimenRequest;
import org.example.labo03.domain.dto.request.UpdateSpecimenRequest;
import org.example.labo03.domain.dto.response.SpecimenResponse;
import org.example.labo03.domain.entity.Specimen;
import org.example.labo03.exception.ResourceNotFoundException;
import org.example.labo03.repository.SpecimenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecimenServiceImplTest {

    @Mock
    private SpecimenRepository specimenRepository;

    @Mock
    private SpecimenMapper specimenMapper;

    @InjectMocks
    private SpecimenServiceImpl specimenService;

    private UUID id;
    private Specimen specimen;
    private SpecimenResponse response;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        specimen = Specimen.builder()
                .id(id).name("Lynel").region("Hebra").dangerLevel(9).isFriendly(false)
                .build();
        response = SpecimenResponse.builder()
                .id(id).name("Lynel").region("Hebra").dangerLevel(9).isFriendly(false)
                .build();
    }

    // ---------- createSpecimen ----------
    @Test
    @DisplayName("createSpecimen: mapea, guarda y devuelve el DTO")
    void createSpecimen_returnsSavedDto() {
        CreateSpecimenRequest request = CreateSpecimenRequest.builder()
                .name("Lynel").region("Hebra").dangerLevel(9).isFriendly(false)
                .build();

        when(specimenMapper.toEntityCreate(request)).thenReturn(specimen);
        when(specimenRepository.save(specimen)).thenReturn(specimen);
        when(specimenMapper.toDto(specimen)).thenReturn(response);

        SpecimenResponse result = specimenService.createSpecimen(request);

        assertNotNull(result);
        assertEquals("Lynel", result.getName());
        verify(specimenRepository).save(specimen);
    }

    // ---------- getSpecimenById ----------
    @Test
    @DisplayName("getSpecimenById: cuando existe, devuelve el DTO")
    void getSpecimenById_found_returnsDto() {
        when(specimenRepository.findById(id)).thenReturn(Optional.of(specimen));
        when(specimenMapper.toDto(specimen)).thenReturn(response);

        SpecimenResponse result = specimenService.getSpecimenById(id);

        assertEquals(id, result.getId());
        verify(specimenRepository).findById(id);
    }

    @Test
    @DisplayName("getSpecimenById: cuando no existe, lanza ResourceNotFoundException")
    void getSpecimenById_notFound_throws() {
        when(specimenRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> specimenService.getSpecimenById(id));

        verify(specimenMapper, never()).toDto(any(Specimen.class));
    }

    // ---------- getAllSpecimens ----------
    @Test
    @DisplayName("getAllSpecimens: con datos, devuelve la pagina mapeada")
    void getAllSpecimens_withData_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Specimen> entityPage = new PageImpl<>(List.of(specimen));
        Page<SpecimenResponse> dtoPage = new PageImpl<>(List.of(response));

        when(specimenRepository.findAll(pageable)).thenReturn(entityPage);
        when(specimenMapper.toDto(entityPage)).thenReturn(dtoPage);

        Page<SpecimenResponse> result = specimenService.getAllSpecimens(pageable);

        assertEquals(1, result.getTotalElements());
        verify(specimenRepository).findAll(pageable);
    }

    @Test
    @DisplayName("getAllSpecimens: sin datos, lanza ResourceNotFoundException")
    void getAllSpecimens_empty_throws() {
        Pageable pageable = PageRequest.of(0, 10);
        when(specimenRepository.findAll(pageable)).thenReturn(Page.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> specimenService.getAllSpecimens(pageable));

        verify(specimenMapper, never()).toDto(any(Page.class));
    }

    // ---------- updateSpecimen ----------
    @Test
    @DisplayName("updateSpecimen: cuando existe, guarda y devuelve el DTO actualizado")
    void updateSpecimen_found_returnsDto() {
        UpdateSpecimenRequest request = UpdateSpecimenRequest.builder()
                .name("Lynel Plateado").region("Hebra").dangerLevel(10).isFriendly(false)
                .build();
        Specimen updatedEntity = Specimen.builder()
                .id(id).name("Lynel Plateado").region("Hebra").dangerLevel(10).isFriendly(false)
                .build();
        SpecimenResponse updatedResponse = SpecimenResponse.builder()
                .id(id).name("Lynel Plateado").region("Hebra").dangerLevel(10).isFriendly(false)
                .build();

        when(specimenRepository.findById(id)).thenReturn(Optional.of(specimen)); // validacion interna
        when(specimenMapper.toDto(specimen)).thenReturn(response);               // <-- AGREGADO: la llamada interna de getSpecimenById
        when(specimenMapper.toEntityUpdate(request, id)).thenReturn(updatedEntity);
        when(specimenRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(specimenMapper.toDto(updatedEntity)).thenReturn(updatedResponse);

        SpecimenResponse result = specimenService.updateSpecimen(id, request);

        assertEquals("Lynel Plateado", result.getName());
        verify(specimenRepository).save(updatedEntity);
    }

    @Test
    @DisplayName("updateSpecimen: cuando no existe, lanza excepcion y no guarda")
    void updateSpecimen_notFound_throws() {
        UpdateSpecimenRequest request = UpdateSpecimenRequest.builder()
                .name("X").region("Y").dangerLevel(1).isFriendly(true)
                .build();
        when(specimenRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> specimenService.updateSpecimen(id, request));

        verify(specimenRepository, never()).save(any(Specimen.class));
    }

    // ---------- deleteSpecimen ----------
    @Test
    @DisplayName("deleteSpecimen: cuando existe, elimina y devuelve el DTO")
    void deleteSpecimen_found_deletesAndReturns() {
        when(specimenRepository.findById(id)).thenReturn(Optional.of(specimen));
        when(specimenMapper.toDto(specimen)).thenReturn(response);

        SpecimenResponse result = specimenService.deleteSpecimen(id);

        assertEquals(id, result.getId());
        verify(specimenRepository).deleteById(id);
    }

    @Test
    @DisplayName("deleteSpecimen: cuando no existe, lanza excepcion y no elimina")
    void deleteSpecimen_notFound_throws() {
        when(specimenRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> specimenService.deleteSpecimen(id));

        verify(specimenRepository, never()).deleteById(any(UUID.class));
    }
}
